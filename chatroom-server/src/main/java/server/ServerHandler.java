package server;

import common.DB;
import common.dao.MessageDao;
import common.dao.UserDao;
import common.model.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    //username-channel
    private static final Map<String, Channel> onlineUser = new LinkedHashMap<>();
    //address-username
    private static final Map<String, String> address2username = new LinkedHashMap<>();
    private static final ObservableList<String> onlineUsername = FXCollections.observableArrayList();
    //group-(username-channel)
    private static final Map<String, Map<String, Channel>> group = new LinkedHashMap<>();
    private Log log;
    private final DB db = new DB("server");
    private final UserDao userDao = db.getDao(UserDao.class);
    private final MessageDao messageDao = db.getDao(MessageDao.class);

    // channel断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String address = ctx.channel().remoteAddress().toString();
        String username = address2username.get(address);
        List<String> friends = userDao.getFriends(username);
        for (String friend : friends) {
            if (onlineUser.containsKey(friend)) {
                onlineUser.get(friend).writeAndFlush(new User(username, "离线", 0));
            }
        }
        log.addLog(username + "下线了");
        Platform.runLater(() -> {
            address2username.remove(address);
            onlineUser.remove(username);
            onlineUsername.remove(username);
        });
    }

    //读取客户端发送的数据
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof User) {
            User user = (User) msg;
            if (onlineUser.containsKey(user.getUsername())) {
                ctx.channel().write(new Response(false, "该用户名已被使用！"));
                return;
            }
            address2username.put(ctx.channel().remoteAddress().toString(), user.getUsername());
            onlineUser.put(user.getUsername(), ctx.channel());
            Platform.runLater(() -> onlineUsername.add(user.getUsername()));
            List<String> users = userDao.getFriends(user.getUsername());
            List<User> friends = new ArrayList<>();
            for (String s : users) {
                if (onlineUser.containsKey(s)) {
                    onlineUser.get(s).writeAndFlush(new User(user.getUsername(), "在线", 0));
                }
                if (onlineUser.containsKey(s)) {
                    friends.add(new User(s, "在线"));
                } else {
                    friends.add(new User(s, "离线"));
                }
            }
            ctx.channel().write(new Response(true, "登陆成功！", friends));
            List<Message> offlineMessage = messageDao.getOfflineMessage(user.getUsername());
            for (Message message : offlineMessage) {
                ctx.channel().writeAndFlush(message);
            }
            messageDao.delete(user.getUsername());
        }
        if (msg instanceof UserOption) {
            UserOption userOption = (UserOption) msg;
            if (userOption.getOp() == -1) {//删除好友
                userDao.delete(userOption.getFrom(), userOption.getTo());
                userDao.delete(userOption.getTo(), userOption.getFrom());
                Channel channel = onlineUser.get(userOption.getFrom());
                if (channel != null) channel.writeAndFlush(new User(userOption.getTo(), "在线", -1));
                Channel channel1 = onlineUser.get(userOption.getTo());
                if (channel1 != null) channel1.writeAndFlush(new User(userOption.getFrom(), "在线", -1));
            } else if (userOption.getOp() == 0) {
                if (onlineUser.containsKey(userOption.getTo())) {
                    onlineUser.get(userOption.getTo()).writeAndFlush(userOption);
                } else {
                    ctx.writeAndFlush(new Response(false, "对方不在线"));
                }
            } else {
                userDao.save(userOption.getFrom(), userOption.getTo());
                userDao.save(userOption.getTo(), userOption.getFrom());
                onlineUser.get(userOption.getFrom()).writeAndFlush(new User(userOption.getTo(), "在线", 1));
                onlineUser.get(userOption.getTo()).writeAndFlush(new User(userOption.getFrom(), "在线", 1));
            }
        }

        if (msg instanceof Message) {
            Message m = (Message) msg;
            Channel channel = onlineUser.get(m.getTo());
            log.addLog(m.getFrom() + "向" + m.getTo() + "发送信息");
            if (channel == null) {
                //不在线时，保存在服务端
                messageDao.save(m);
            } else {
                channel.writeAndFlush(msg);
            }
        }
        if (msg instanceof UploadFile) {
            UploadFile uploadFile = (UploadFile) msg;
            Channel channel = onlineUser.get(uploadFile.getTo());
            log.addLog(uploadFile.getFrom() + "向" + uploadFile.getTo() + "发送文件");
            if (channel == null) {
                log.addLog("文件发送失败：" + uploadFile.getTo() + "不在线");
                ctx.channel().writeAndFlush(new Response(false, "对方不在线！"));
            } else {
                channel.writeAndFlush(uploadFile);
            }
        }

        if (msg instanceof GroupMessage) {
            GroupMessage groupMessage = (GroupMessage) msg;
            if (groupMessage.getFlag() == 1) {
                //加入群聊
                //如果没有群，创建
                group.computeIfAbsent(groupMessage.getGroup(), k -> new LinkedHashMap<>());
                group.get(groupMessage.getGroup()).put(groupMessage.getFrom(), ctx.channel());
            } else if (groupMessage.getFlag() == -1) {
                //退出群聊
                group.get(groupMessage.getGroup()).remove(groupMessage.getFrom());
            } else {
                //发消息
                Collection<Channel> channels = group.get(groupMessage.getGroup()).values();
                for (Channel channel : channels) {
                    if (!channel.equals(ctx.channel())) {
                        channel.writeAndFlush(groupMessage);
                    }
                }
            }
        }
    }

    //读取完毕客户端发送过来的数据之后的操作
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("服务端接收数据完毕..");
        ctx.flush();
    }

    /**
     * 功能：服务端发生异常的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
        log.addLog("异常信息：  " + cause.getMessage());
    }

    public static ObservableList<String> getOnlineUsername() {
        return onlineUsername;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public interface Log {
        void addLog(String msg);
    }
}
