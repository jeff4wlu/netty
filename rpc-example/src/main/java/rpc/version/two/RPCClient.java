package rpc.version.two;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.apache.log4j.Logger;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
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
                                     final InetSocketAddress serverIP,
                                     final RPCClientChannelPoolRepo poolRepo) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass}, (proxy, method, args) -> {

                    SyncFuture syncFuture = null;
                    String uid = null;

                    SimpleChannelPool pool = poolRepo.poolMap.get(serverIP);
                    Future<Channel> f = pool.acquire();//多线程下是否安全？？？
                    f.get();//本来也可以用addlistner的方式,但不清楚怎样传出uid和syncfuture
                    if(f.isSuccess()){
                        uid = UnicodeUtils.randomBaseID(8);
                        syncFuture = new SyncFuture();
                        Channel ch = f.getNow();
                        //发送请求
                        RPCRequest req = new RPCRequest();
                        req.setMsgId(uid);
                        req.setServiceName(serviceClass.getName());
                        req.setMethodName(method.getName());
                        Class[] paramTypes = method.getParameterTypes();
                        req.setParamsType(paramTypes);
                        req.setParamsValue(args);

                        RPCClientChannelPoolRepo.futureMap.put(uid, syncFuture);
                        ch.writeAndFlush(req);//异步动作

                        pool.release(ch);
                    }

                    Object obj = syncFuture.get();
                    RPCClientChannelPoolRepo.futureMap.remove(uid);
                    return obj;


                });
    }

}



