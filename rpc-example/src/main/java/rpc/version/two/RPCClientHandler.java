package rpc.version.two;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-15 15:10
 */
public class RPCClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(RPCClientHandler.class);

    //收到服务端数据，唤醒等待线程
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RPCResponse response = ((RPCResponse)msg);
        SyncFuture f = RPCClientChannelPoolRepo.futureMap.get(response.getMsgId());
        if(f != null)
            f.setResponse(response.getResult());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.flush();
        ctx.close();
    }
}