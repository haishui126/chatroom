package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.application.Platform;
import javafx.beans.binding.When;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {

    public TextField portTF;
    public Button btn;
    public ListView<String> userListView;
    public ListView<String> logListView;

    private final BooleanProperty runningProperty = new SimpleBooleanProperty(false);
    private EventLoopGroup bossGroup;
    private EventLoopGroup group;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userListView.setItems(ServerHandler.getOnlineUsername());
        btn.textProperty().bind(new When(runningProperty).then("停止").otherwise("启动"));
        Platform.runLater(() -> {
            btn.getScene().getWindow().setOnCloseRequest(windowEvent -> {
                if (bossGroup != null) {
                    bossGroup.shutdownGracefully();
                }
                if (group != null) {
                    group.shutdownGracefully();
                }
            });
        });
    }

    public void btnClick() {
        if (runningProperty.get()) {
            bossGroup.shutdownGracefully();
            group.shutdownGracefully();
            runningProperty.set(false);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portTF.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("端口号必须为整数");
            alert.show();
            return;
        }
        runningProperty.set(true);
        bossGroup = new NioEventLoopGroup();

        group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(port)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            ServerHandler serverHandler = new ServerHandler();
                            serverHandler.setLog(msg -> Platform.runLater(() -> {
                                logListView.getItems().add(msg);
                                logListView.scrollTo(msg);
                            }));
                            ch.pipeline().addLast(serverHandler); // 客户端触发操作
                            ch.pipeline().addLast(new ObjectEncoder());
                        }
                    });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(getClass() + " 启动正在监听： " + cf.channel().localAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
