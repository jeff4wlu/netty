import org.junit.Test;
import rpc.version.common.HelloService;
import rpc.version.common.HelloServiceImpl;
import rpc.version.common.Person;
import rpc.version.common.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

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
                Server serviceServer = new rpc.version.one.ServiceCenter(8088);
                //注册本服务器所提供的RPC服务
                serviceServer.register(HelloService.class, HelloServiceImpl.class);
                serviceServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();//生成环境是可以不开线程跑的，因为server里面也开了线程，但测试环境中由于要在下面跑RPCClient，不开线程会阻塞跑不到下面。

        HelloService service = rpc.version.one.RPCClient.getRemoteProxyObj(HelloService.class, new InetSocketAddress("localhost", 8088));
        System.out.println(service.sayHi("test"));

    }

    @Test
    public void versionTwoTest() {
        new Thread(() -> {
            try {
                Server serviceServer = new rpc.version.two.ServiceCenter(8088);
                //注册本服务器所提供的RPC服务
                serviceServer.register(HelloService.class, HelloServiceImpl.class);
                serviceServer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();//生成环境是可以不开线程跑的，因为server里面也开了线程，但测试环境中由于要在下面跑RPCClient，不开线程会阻塞跑不到下面。

        HelloService service = (HelloService)rpc.version.two.RPCClient.createProxy(HelloService.class, "localhost",8088);

        Person p = new Person();
        p.setAge(12);
        p.setName("lwj");
        p.setPhone("132323242");
        Person res = service.handlePerson(p);
        System.out.println("====================");
        System.out.println(res.toString());
        System.out.println(service.sayHi("asdf"));

    }

}
