package rpc.version.two;

import lombok.Data;

import java.util.List;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-17 10:53
 */
@Data
public class RPCRequest{
    private String serviceName;
    private String methodName;
    private Class[] paramsType;
    private Object[] paramsValue;
}