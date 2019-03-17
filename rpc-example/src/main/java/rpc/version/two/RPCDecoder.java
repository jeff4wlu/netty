package rpc.version.two;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.MessageToMessageDecoder;
import rpc.version.common.ProtostuffUtil;

import java.util.List;

/**
 * @author Lu Weijian
 * @description 用处是将读取的byte数据转化为用户自己定义的数据类型
 * 需要指定给解码器具体的解码对象类型，这样就通用了。
 * @email lwj@kapark.cn
 * @date 2019-03-17 11:53
 */
class RPCDecoder extends MessageToMessageDecoder<ByteBuf> {

    // 需要反序列对象所属的类型
    private Class<?> genericClass;

    // 构造方法，传入需要反序列化对象的类型
    public RPCDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // ByteBuf的长度
        int length = msg.readableBytes();
        if(length == 0) return;
        // 构建length长度的字节数组
        byte[] pkg = new byte[length];
        // 将ByteBuf数据复制到字节数组中
        msg.readBytes(pkg);
        // 反序列化对象
        Object obj = ProtostuffUtil.deserialize(pkg, this.genericClass);
        // 添加到反序列化对象结果列表
        out.add(obj);
    }

}
