package rpc.version.two;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import rpc.version.common.ProtostuffUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lu Weijian
 * @description 用处是将读取的byte数据转化为用户自己定义的数据类型
 * 需要指定给解码器具体的解码对象类型，这样就通用了。
 * @email lwj@kapark.cn
 * @date 2019-03-17 11:53
 */
class RPCDecoder extends ByteToMessageDecoder {

    // 需要反序列对象所属的类型
    //decoder需要对request和response两类信息解码，对应于服务器和客户端
    //为了统一处理，所以在初始化线程池时指定解码对象。这个算是线程安全（因为服务器和客户端是两个不同的进程，设置好就不变了）
    private Class<?> genericClass;

    // 构造方法，传入需要反序列化对象的类型
    public RPCDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /*
        if (in.readableBytes() < 4) {
            return;
        }

        int tmp = in.readableBytes();


        in.markReaderIndex();
        int dataLength = in.readInt();
        System.out.println("----------------------");
        System.out.println("可读长度是：" + tmp + "； 长度是：" + dataLength);
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = ProtostuffUtil.deserialize(data, genericClass);
        out.add(obj);*/


        // ByteBuf的长度
        int length = in.readableBytes();
        if(length == 0) return;
        // 构建length长度的字节数组
        byte[] pkg = new byte[length];
        // 将ByteBuf数据复制到字节数组中
        in.readBytes(pkg);
        // 反序列化对象
        Object obj = ProtostuffUtil.deserialize(pkg, this.genericClass);
        // 添加到反序列化对象结果列表
        out.add(obj);
    }

}
