package client;

import common.model.Response;
import common.model.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static ClientHandler handler = null;
    private LoginResponseListener loginResponseListener;
    private MessageListener messageListener;
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

    public interface LoginResponseListener {
        void onReceive(Response response);
    }

    public interface MessageListener {
        void onReceive(Message message);
    }

    public void sendMessage(Message message) {
        channel.writeAndFlush(message);
    }
}
