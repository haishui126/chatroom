package server;

import common.model.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    //username-channel
    private static final Map<String, Channel> onlineUser = new LinkedHashMap<>();
    //address-username
    private static final Map<String, String> address2username = new LinkedHashMap<>();
    private static final ObservableList<String> onlineUsername = FXCollections.observableArrayList();
    //group-(username-channel)
    private static final Map<String, Map<String, Channel>> group = new LinkedHashMap<>();
    private Log log;

    // channel断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String address = ctx.channel().remoteAddress().toString();
        String username = address2username.get(address);
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
            ctx.channel().write(new Response(true, "登陆成功！"));
        }
        if (msg instanceof Message) {
            Message m = (Message) msg;
            Channel channel = onlineUser.get(m.getTo());
            log.addLog(m.getFrom() + "向" + m.getTo() + "发送信息");
            if (channel == null) {
                log.addLog("消息发送失败：" + m.getTo() + "不在线");
                ctx.channel().writeAndFlush(new Response(false, "对方不在线！"));
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
