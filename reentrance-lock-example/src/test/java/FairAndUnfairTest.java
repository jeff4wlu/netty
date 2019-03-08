import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-07 16:54
 */
public class FairAndUnfairTest {

    @Test
    public void FairLockTest(){
        Lock fairLock = new FairAndUnfairLock.MyReentrantLock(true);
        FairAndUnfairLock.testLock(fairLock);
    }

    @Test
    public void UnfairLockTest(){
        Lock fairLock = new FairAndUnfairLock.MyReentrantLock(false);
        FairAndUnfairLock.testLock(fairLock);
    }

}
