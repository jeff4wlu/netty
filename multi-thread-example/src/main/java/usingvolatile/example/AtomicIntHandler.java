package usingvolatile.example;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-12 14:01
 */
public class AtomicIntHandler implements Runnable {
    public static AtomicInteger i = new AtomicInteger(0);

    public static void addI(){
        i.incrementAndGet();
    }
    public static int getI(){
        return i.get();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AtomicIntHandler.addI();

    }
}
