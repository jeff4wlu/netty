import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Lu Weijian
 * @description 客户的业务逻辑都放handler上，本例
 * 只是简单地接收客户端数据并打印屏幕
 * @email lwj@kapark.cn
 * @date 2019-03-05 19:39
 */
public class SimpleHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ByteBuf,类似于NIO中的ByteBuffer,但是更强大
        ByteBuf reqBuf = (ByteBuf) msg;
        //获取请求字符串
        String req = Utils.getReq(reqBuf);
        System.out.println(req);


/*
        if ("GET TIME".equals(req)){
            String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new Date());
            String resStr = "当前时间:" + timeNow;

            //获取发送给客户端的数据
            ByteBuf resBuf = getRes(resStr);

            logger.debug("服务端应答数据:\n" + resStr);
            ctx.write(resBuf);
        }else {
            //丢弃
            logger.debug("丢弃");
            ReferenceCountUtil.release(msg);
        }*/
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将消息发送队列中的消息写入到SocketChannel中发送给对方
        //logger.debug("channelReadComplete");
        System.out.println("channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常时,关闭 ChannelHandlerContext,释放ChannelHandlerContext 相关的句柄等资源
        //logger.error("exceptionCaught");
        System.out.println("exceptionCaught");
        ctx.close();
    }



}
