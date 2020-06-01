package client;

import common.LoginResponse;
import common.Message;
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

    //    /**
//     * 向服务端发送数据
//     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
        System.out.println("客户端与服务端通道-开启：" + ctx.channel().localAddress() + "channelActive");
    }

//    /**
//     * channelInactive
//     * <p>
//     * channel 通道 Inactive 不活跃的
//     * <p>
//     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
//     */
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("客户端与服务端通道-关闭：" + ctx.channel().localAddress() + "channelInactive");
//    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
        if (msg instanceof LoginResponse) {
            Platform.runLater(() -> loginResponseListener.onReceive((LoginResponse) msg));
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
        void onReceive(LoginResponse response);
    }

    public interface MessageListener {
        void onReceive(Message message);
    }

    public void sendMessage(Message message) {
        channel.writeAndFlush(message);
    }
}
