//package client;
//
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.serialization.ClassResolvers;
//import io.netty.handler.codec.serialization.ObjectDecoder;
//import io.netty.handler.codec.serialization.ObjectEncoder;
//import io.netty.handler.stream.ChunkedWriteHandler;
//import io.netty.util.AttributeKey;
//
//import java.net.InetSocketAddress;
//
//public class EchoClient {
//    private final String host;
//    private final int port;
//
//    public EchoClient() {
//        this(0);
//    }
//
//    public EchoClient(int port) {
//        this("localhost", port);
//    }
//
//    public EchoClient(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }
//
//    public void start() throws Exception {
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap b = new Bootstrap();
//            b.group(group) // 注册线程池
//                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
//                    .channel(NioSocketChannel.class) // 使用NioSocketChannel来作为连接用的channel类
//                    .remoteAddress(new InetSocketAddress(this.host, this.port)) // 绑定连接端口和host信息
//                    .handler(new ChannelInitializer<SocketChannel>() { // 绑定连接初始化器
//                        @Override
//                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.attr(AttributeKey.newInstance("username")).set("海水");
//                            System.out.println("正在连接中...");
//                            ch.pipeline().addLast(new ObjectEncoder());
//                            ch.pipeline().addLast(new EchoClientHandler());
//                            ch.pipeline().addLast(new ObjectDecoder(1024 * 1024, ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
//                            ch.pipeline().addLast(new ChunkedWriteHandler());
//                        }
//                    });
//            // System.out.println("服务端连接成功..");
//            ChannelFuture cf = b.connect().sync(); // 异步连接服务器
////            cf.channel()
//            System.out.println("服务端连接成功..."); // 连接完成
//            cf.channel().closeFuture().sync(); // 异步等待关闭连接channel
//            System.out.println("连接已关闭.."); // 关闭完成
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            group.shutdownGracefully().sync(); // 释放线程池资源
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        new EchoClient("127.0.0.1", 8888).start(); // 连接127.0.0.1/65535，并启动
//    }
//}
