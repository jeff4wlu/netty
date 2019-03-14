import org.junit.Test;
import packagesplicing.example.BadTimeClient;
import packagesplicing.example.BadTimeServer;
import packagesplicing.example.GoodTimeClient;
import packagesplicing.example.GoodTimeServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-14 11:38
 */
public class PackageSplicingTest {

    //没做分包处理的场景
    @Test
    public void noPackageSplicingTest(){
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
        taskExecutor.execute(new BadTimeServer());
        taskExecutor.execute(new BadTimeClient());
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

    //做了分包处理的场景
    @Test
    public void packageSplicingTest(){
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);
        taskExecutor.execute(new GoodTimeServer());
        taskExecutor.execute(new GoodTimeClient());
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
