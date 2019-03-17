package protobuf.simple.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Logger;

/**
 * @author Lu Weijian
 * @description 本服务器只是接收一个简单的POJO传送，而不是RPC框架的那种需要传服务名、方法名、参数等
 * @email lwj@kapark.cn
 * @date 2019-03-14 19:37
 */
public class EchoPOJOServer implements Runnable {

    @Override
    public void run() {
        int port = 8081;
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.DEBUG));

            b.childHandler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));// 根据 分隔符（换行符）分割，分割后还是一个 ByteBuf
                    ch.pipeline().addLast(new MsgDecoder());//  客户需要发送信息， ByteBuf 转化为 POJO
                    ch.pipeline().addLast(
                            new EchoServerHandler()); // 业务处理类
                }
            });
            ChannelFuture cf = b.bind(port).sync();
            cf.channel().closeFuture().sync();
            // Wait until the server socket is closed.
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    //final类似全局变量
    private static final Logger logger = Logger.getLogger(EchoPOJOClient.class.getName());

    //业务处理handler
    class EchoServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("server read......" + msg.toString());
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.info("chanle is closed ");
        }
    }

    //  bytebuf  转化为  POJO ，  处理io 事件，继承ChannelInboundHandlerAdapter
    class MsgDecoder extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf bytebuf = (ByteBuf) msg;
            // bytebuf.array();// 报错 java.lang.UnsupportedOperationException: direct buffer  还没有分析到位
            byte[] b = new byte[bytebuf.readableBytes()];
            bytebuf.readBytes(b);
            Msg.Person xxg2 = Msg.Person.parseFrom(b);
            logger.info("MsgDecoder................." + xxg2.toString());
            ctx.fireChannelRead(xxg2);
        }


    }
}
