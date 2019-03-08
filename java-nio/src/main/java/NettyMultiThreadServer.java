import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * @author Lu Weijian
 * @description Netty多线程reactor模型的服务器
 * @email lwj@kapark.cn
 * @date 2019-03-05 19:20
 */
public class NettyMultiThreadServer implements Runnable {

    @Override
    public void run() {
        //EventLoopGroup reactorGroup = new NioEventLoopGroup();
        EventLoopGroup acceptorGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup ioGroup = new NioEventLoopGroup(9);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(acceptorGroup, ioGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SimpleHandlerInitializer());

            Channel ch = b.bind(8190).sync().channel();
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            acceptorGroup.shutdownGracefully();
            ioGroup.shutdownGracefully();
        }

    }
}
