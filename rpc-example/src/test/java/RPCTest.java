import org.junit.Test;
import rpc.version.one.*;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-13 9:01
 */
public class RPCTest {

    @Test
    public void versionOneTest() {
        new Thread(() -> {
                try {
                    Server serviceServer = new ServiceCenter(8088);
                    serviceServer.register(HelloService.class, HelloServiceImpl.class);
                    serviceServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }).start();
        HelloService service = RPCClient.getRemoteProxyObj(HelloService.class, new InetSocketAddress("localhost", 8088));
        System.out.println(service.sayHi("test"));

    }
}
