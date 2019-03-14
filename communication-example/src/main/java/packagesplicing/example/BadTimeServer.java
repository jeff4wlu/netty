package packagesplicing.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
/**
 * @author Lu Weijian
 * @description 一个没有处理粘包粘包的echo服务器
 * @email lwj@kapark.cn
 * @date 2019-03-14 11:19
 */
public class BadTimeServer implements Runnable {

    private final static int PORT = 8080;

    @Override
    public void run(){
        final BadTimeServerHandler serverHandler = new BadTimeServerHandler();
        // 创建EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 创建EventLoopGroup
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                //指定所使用的NIO传输Channel
                .channel(NioServerSocketChannel.class)
                //使用指定的端口设置套接字地址
                .localAddress(new InetSocketAddress(PORT))
                // 添加一个EchoServerHandler到Channle的ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //EchoServerHandler被标注为@shareable,所以我们可以总是使用同样的案例
                        socketChannel.pipeline().addLast(serverHandler);
                    }
                });

        try {
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

