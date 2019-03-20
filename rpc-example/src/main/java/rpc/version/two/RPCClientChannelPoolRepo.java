package rpc.version.two;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lu Weijian
 * @description 链接池管理器
 * @email lwj@kapark.cn
 * @date 2019-03-18 20:52
 */
public class RPCClientChannelPoolRepo {

    final static private EventLoopGroup group = new NioEventLoopGroup();
    final static private Bootstrap strap = new Bootstrap();

    //它是线程安全的
    public static ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap;
    public static ConcurrentHashMap<String, SyncFuture> futureMap;

    public static void build() throws Exception {

        futureMap = new ConcurrentHashMap<>();

        strap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(strap.remoteAddress(key), new RPCClientChannelPoolHandler(), 2);
            }
        };
    }

}
