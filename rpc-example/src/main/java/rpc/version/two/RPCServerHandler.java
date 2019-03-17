package rpc.version.two;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-17 12:12
 */
class RPCServerHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(RPCServerHandler.class);

    private HashMap<String, Class> serviceRegistry;

    public RPCServerHandler(HashMap<String, Class> serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("server read......" + msg.toString());

        RPCRequest req = (RPCRequest)msg;
        String serviceName = req.getServiceName();
        String methodName = req.getMethodName();
        Class[] paramsType = req.getParamsType();
        Object[] paramsValue = req.getParamsValue();

        Class serviceClass = serviceRegistry.get(serviceName);
        if (serviceClass == null) {
            throw new ClassNotFoundException(serviceName + " not found");
        }

        Method method = serviceClass.getMethod(methodName, paramsType);
        Object result = method.invoke(serviceClass.newInstance(), paramsValue);

        RPCResponse res = new RPCResponse();
        res.setCode(200);
        res.setResult(result);
        ctx.writeAndFlush(res);
        ctx.close();//是否能关闭TCP链接，从而可以通知对方接受结果
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel is closed ");
    }

}
