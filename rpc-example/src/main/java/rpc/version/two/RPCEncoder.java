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
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 直接生成序列化对象
        // 需要注意的是，使用protostuff序列化时，不需要知道pojo对象的具体类型也可以进行序列化时
        // 在反序列化时，只要提供序列化后的字节数组和原来pojo对象的类型即可完成反序列化
        byte[] pkg = ProtostuffUtil.serialize(msg);
        logger.info("RPCRequest Encoding .................");
        out.writeBytes(pkg);
    }

}
