package rpc.version.two;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;
import rpc.version.common.*;

/**
 * @author Lu Weijian
 * @description 用于将用户定义的类型转化为byte类型
 * @email lwj@kapark.cn
 * @date 2019-03-15 15:11
 */
public class RPCEncoder extends MessageToByteEncoder<Object> {

    private static final Logger logger = Logger.getLogger(RPCEncoder.class.getName());

    @Override
    protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {

        byte[] data = ProtostuffUtil.serialize(in);
        out.writeInt(data.length);
        out.writeBytes(data);
    }

}
