package client;

import common.model.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static ClientHandler handler = null;
    private LoginResponseListener loginResponseListener;
    private MessageListener messageListener;
    private FileListener fileListener;
    private GroupMessageListener groupMessageListener;
    private UserListener userListener;
    private UserOptionListener userOptionListener;
    private static Channel channel;

    private ClientHandler() {
    }

    public static ClientHandler getInstance() {
        if (handler == null) {
            handler = new ClientHandler();
        }
        return handler;
    }

    public static void resetInstance() {
        handler = null;
    }

    //连接时
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        System.out.println("客户端与服务端通道-开启：" + ctx.channel().localAddress() + "channelActive");
    }

    //读取信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Response) {
            Platform.runLater(() -> loginResponseListener.onReceive((Response) msg));
        } else if (msg instanceof Message) {
            Platform.runLater(() -> messageListener.onReceive((Message) msg));
        } else if (msg instanceof UploadFile) {
            Platform.runLater(() -> fileListener.onReceive((UploadFile) msg));
        } else if (msg instanceof GroupMessage) {
            Platform.runLater(() -> groupMessageListener.onReceive((GroupMessage) msg));
        } else if (msg instanceof User) {
            Platform.runLater(() -> userListener.onReceive((User) msg));
        } else if (msg instanceof UserOption) {
            Platform.runLater(() -> userOptionListener.onReceive((UserOption) msg));
        } else {
            throw new Exception("未知的消息类型！");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        System.out.println("异常退出:" + cause.getMessage());
        cause.printStackTrace();
    }

    public void setLoginResponseListener(LoginResponseListener loginResponseListener) {
        this.loginResponseListener = loginResponseListener;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setFileListener(FileListener fileListener) {
        this.fileListener = fileListener;
    }

    public void setGroupMessageListener(GroupMessageListener groupMessageListener) {
        this.groupMessageListener = groupMessageListener;
    }

    public interface LoginResponseListener {
        void onReceive(Response response);
    }

    public interface MessageListener {
        void onReceive(Message message);
    }

    public interface FileListener {
        void onReceive(UploadFile file);
    }

    public interface GroupMessageListener {
        void onReceive(GroupMessage message);
    }

    public interface UserListener {
        void onReceive(User user);
    }

    public interface UserOptionListener {
        void onReceive(UserOption userOption);
    }

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    public void setUserOptionListener(UserOptionListener userOptionListener) {
        this.userOptionListener = userOptionListener;
    }

    public void sendMessage(Message message) {
        channel.writeAndFlush(message);
    }

    public void sendGroupMessage(GroupMessage message) {
        channel.writeAndFlush(message);
    }

    public void sendFile(UploadFile file) {
        channel.writeAndFlush(file);
    }

    public void sendUserOption(UserOption userOption) {
        channel.writeAndFlush(userOption);
    }
}
