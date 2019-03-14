import org.junit.Test;
import packagesplicing.example.BadTimeClient;
import packagesplicing.example.BadTimeServer;
import protobuf.simple.example.EchoPOJOClient;
import protobuf.simple.example.EchoPOJOServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-14 19:59
 */
public class ProtobufTest {

    @Test
    public void simpleEchoPOJOTest(){
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
        taskExecutor.execute(new EchoPOJOServer());
        taskExecutor.execute(new EchoPOJOClient());
        taskExecutor.shutdown();//关闭管理器，但允许已经执行的线程继续执行
        try {
            //循环查询管理器是否已经关闭
            while (!taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
