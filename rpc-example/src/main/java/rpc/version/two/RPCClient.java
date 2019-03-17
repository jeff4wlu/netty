package rpc.version.two;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Lu Weijian
 * 客户端的远程代理对象
 * 本类只处理对服务端的链接事宜，发送数据由RPCClientHandler处理
 * 向服务器传送一个协议对象（服务名、方法名、参数类型、参数值）
 * 当调用代理方法的时候，我们需要初始化 Netty 客户端，还需要向服务端请求数据，并返回数据。
 * lwj@kapark.cn
 * 2019-03-13 8:59
 */
public class RPCClient {

    private static final Logger logger = Logger.getLogger(RPCClientHandler.class);

    private static ExecutorService executor = Executors
            .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //创建一个代理对象
    public static Object createProxy(final Class<?> serviceClass,
                                     final String serverIp,
                                     final int port) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass}, (proxy, method, args) -> {

                    RPCClientHandler client = new RPCClientHandler();
                    EventLoopGroup workerGroup = new NioEventLoopGroup();
                    try {
                        Bootstrap b = new Bootstrap();
                        b.group(workerGroup).channel(NioSocketChannel.class);
                        b.option(ChannelOption.TCP_NODELAY, true);
                        b.handler(new ChannelInitializer<SocketChannel>() {
                            public void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new RPCDecoder(RPCResponse.class));
                                ch.pipeline().addLast(new RPCEncoder());
                                ch.pipeline().addLast(client); //千万小心，不要再new一个RPCClientHandler出来
                            }
                        });

                        Channel channel = b.connect(serverIp, port).sync().channel();

                        //发送请求
                        RPCRequest req = new RPCRequest();
                        req.setServiceName(serviceClass.getName());
                        req.setMethodName(method.getName());
                        Class[] paramTypes = method.getParameterTypes();
                        req.setParamsType(paramTypes);
                        req.setParamsValue(args);

                        channel.writeAndFlush(req).sync();
                        channel.closeFuture().sync();//只有TCP链接关闭才会通知closefuture,本应用是服务器主动关闭链接
                        return client.getResponse();

                    } finally {
                        // Shut down all event loops to terminate all threads.
                        workerGroup.shutdownGracefully();
                    }

                });
    }

}



