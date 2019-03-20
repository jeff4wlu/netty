package rpc.version.two;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author Lu Weijian
 * @description 负责channel的管理
 * @email lwj@kapark.cn
 * @date 2019-03-18 20:45
 */
public class RPCClientChannelPoolHandler implements ChannelPoolHandler {
    @Override
    public void channelReleased(Channel ch) throws Exception {
        System.out.println("channelReleased. Channel ID: " + ch.id());
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        System.out.println("channelAcquired. Channel ID: " + ch.id());
    }

    @Override
    public void channelCreated(Channel ch) throws Exception {

        System.out.println("channelCreated. Channel ID: " + ch.id());
        SocketChannel channel = (SocketChannel) ch;
        channel.config().setKeepAlive(true);
        channel.config().setTcpNoDelay(true);
        channel.pipeline()
                //LengthFieldBasedFrameDecoder从字节缓冲器中提取一个带长度字段的数据包，拆掉长度字段后把真正内容提交给RPCDecoder
                .addLast(new LengthFieldBasedFrameDecoder(2048, 0, 4, 0, 4))
                .addLast(new RPCDecoder(RPCResponse.class))
                .addLast(new RPCEncoder())
                .addLast(new RPCClientHandler());

    }
}
