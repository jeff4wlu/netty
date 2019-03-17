package rpc.version.common;

import java.io.IOException;

/**
 * @author Lu Weijian
 * @description 服务中心接口
 * @email lwj@kapark.cn
 * @date 2019-03-13 8:56
 */
public interface Server {
    public void stop();

    public void start() throws IOException;

    public void register(Class serviceInterface, Class impl);

    public boolean isRunning();

    public int getPort();
}
