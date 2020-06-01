package client.controller;

import client.App;
import client.ClientHandler;
import client.util.AlertUtil;
import common.User;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetSocketAddress;

public class LoginController {
    public TextField ipTF;
    public TextField portTF;
    public Button btn;
    public TextField usernameTF;

    public void onConnect() {
        String host = ipTF.getText();
        int port;
        try {
            port = Integer.parseInt(portTF.getText());
        } catch (NumberFormatException e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "端口号必须为整数！");
            return;
        }
        String username = usernameTF.getText();
        if (username.isEmpty()) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "请输入用户昵称！");
            return;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        //关闭窗口后断开连接
        ClientHandler clientHandler = ClientHandler.getInstance();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group) // 注册线程池
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
                    .remoteAddress(new InetSocketAddress(host, port)) // 绑定连接端口和host信息
                    .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("正在连接中...");
                            ch.pipeline().addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            ch.pipeline().addLast(clientHandler);
                            ch.pipeline().addLast(new ObjectEncoder());
                        }
                    });
            ChannelFuture cf = b.connect().sync(); // 异步连接服务器
            cf.channel().writeAndFlush(new User(username));
            System.out.println("服务端连接成功..."); // 连接完成
            Platform.runLater(() -> btn.getScene().getWindow().setOnCloseRequest(windowEvent -> {
                cf.channel().close();
                group.shutdownGracefully();
            }));
            clientHandler.setLoginResponseListener(response -> Platform.runLater(() -> {
                MainController.setUsername(username);
                if (response.isSuccess()) {
                    try {
                        App.setRoot("main");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    AlertUtil.showAlert(Alert.AlertType.ERROR, response.getMsg());
                    ClientHandler.resetInstance();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
            ClientHandler.resetInstance();
        }
    }
}
