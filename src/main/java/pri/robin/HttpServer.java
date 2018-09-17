package pri.robin;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;


/**
 * @author liangty1
 * 使用netty 处理fullhttprequest和FullHttpResponse
 */
public class HttpServer {
    private final int port;

    HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        int port = 8888;
        new HttpServer(port).start();
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        //线程组，用来进行网络读写等
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        System.out.println("initing channels ..." + socketChannel);
                        socketChannel.pipeline()
                                .addLast("decoder", new HttpRequestDecoder())
//                                .addLast("encoder", new HttpResponseEncoder())
                                //消息聚合器，将消息聚集为一个FullHttpRequest
                                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                .addLast("handler", new ServerHandler());
                    }
                })
                /**
                 * BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，
                 * 用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，将使用默认值50。
                 * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
                 * 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                 */
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}