package server;

import common.LoginResponse;
import common.Message;
import common.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Map<String, Channel> onlineUser = new LinkedHashMap<>();
    private static final Map<String, String> address2username = new LinkedHashMap<>();
    private static final ObservableList<String> onlineUsername = FXCollections.observableArrayList();
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
                ctx.channel().write(new LoginResponse(false, "该用户名已被使用！"));
                return;
            }
            address2username.put(ctx.channel().remoteAddress().toString(), user.getUsername());
            onlineUser.put(user.getUsername(), ctx.channel());
            Platform.runLater(() -> onlineUsername.add(user.getUsername()));
            ctx.channel().write(new LoginResponse(true, "登陆成功！"));
        }
        if (msg instanceof Message) {
            Message m = (Message) msg;
            Channel channel = onlineUser.get(m.getTo());
            log.addLog(m.getFrom() + "向" + m.getTo() + "发送信息");
            if (channel == null) {
                log.addLog("消息发送失败：" + m.getTo() + "不在线");
                ctx.channel().writeAndFlush(new LoginResponse(false, "对方不在线！"));
            } else {
                channel.writeAndFlush(msg);
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
