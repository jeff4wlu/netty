package protobuf.simple.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-14 19:33
 */
public class EchoPOJOClient implements Runnable {

    @Override
    public void run() {
        String host = "127.0.0.1";
        int port = 8081;
        // Configure the client.
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup).channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new MsgEncoder());// 用户outbould ，也就是write()     , POJO转化为ByteBuf
                    ch.pipeline().addLast(
                            new EchoClientHander());
                }
            });
            ChannelFuture cf = b.connect(host, port).sync();
            sendTOServer(cf.channel());
            // Wait until the server socket is closed.
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Shut down all event loops to terminate all threads.
            workerGroup.shutdownGracefully();
        }
    }

    //final类似全局变量
    private static final Logger logger = Logger.getLogger(EchoPOJOClient.class.getName());

    public void sendTOServer(Channel ch) throws InterruptedException, IOException {

        // Read commands from the stdin.
        System.out.println("Enter text (quit to end)");
        ChannelFuture lastWriteFuture = null;

        // 按照定义的数据结构，创建一个Person
        Msg.Person.Builder personBuilder = Msg.Person.newBuilder();
        personBuilder.setId(1);
        personBuilder.setName("sdfsdfsf");
        personBuilder.setEmail("xxg@163.com");
        Msg.Person msg = personBuilder.build();

        //5s 发送一次
        for (; ; ) {
            sleep(5000);//
            ch.writeAndFlush(msg);

        }
    }

    //业务处理handler
    class EchoClientHander extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info(msg.toString());
        }

    }

    //   POJO 转化为   bytebuf，  处理用户事件 所以继承ChannelOutboundHandlerAdapter
    class MsgEncoder extends ChannelOutboundHandlerAdapter {

        String hello = "hello from client";

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Msg.Person p = (Msg.Person) msg;

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(p.toByteArray());
            byteBuf.writeBytes("\r\n".getBytes()); //加上换行符
            logger.info("MsgEncoder.................");
            ctx.write(byteBuf, promise);// 需要再次写入
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            super.flush(ctx);
        }
    }
}
