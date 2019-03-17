package rpc.version.two;

import lombok.Data;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-17 10:53
 */
@Data
public class RPCResponse{
    private int code;
    private String error;
    private Object result;
}
