package rpc.version.two;

import com.google.protobuf.ByteString;
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
import rpc.version.common.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-13 8:57
 */
public class ServiceCenter implements Server {
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static final HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();

    private static boolean isRunning = false;

    private static int port;

    public ServiceCenter(int port) {
        this.port = port;
    }

    public void stop() {
        isRunning = false;
        executor.shutdown();
    }

    //2版本改为使用netty的NIO多线程模型开启服务器
    public void start() throws IOException {
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            b.handler(new LoggingHandler(LogLevel.DEBUG));

            b.childHandler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    //ch.pipeline().addLast(new LineBasedFrameDecoder(1024));// 根据 分隔符（换行符）分割，分割后还是一个 ByteBuf
                    ch.pipeline().addLast(new RPCEncoder());
                    ch.pipeline().addLast(new RPCDecoder(RPCRequest.class));
                    ch.pipeline().addLast(new RPCServerHandler(serviceRegistry)); // 业务处理类
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
    private static final Logger logger = Logger.getLogger(ServiceCenter.class.getName());

    public void register(Class serviceInterface, Class impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getPort() {
        return port;
    }

}
