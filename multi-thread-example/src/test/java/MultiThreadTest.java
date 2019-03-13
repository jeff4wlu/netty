import cas.performance.example.CasCounter;
import cas.performance.example.CountBase;
import cas.performance.example.SyncCounter;
import current.thread.example.CountOperate;
import custom.lock.aqs.example.SharedResource;
import org.junit.Test;
import usingvolatile.example.AccessibleHandler;
import usingvolatile.example.AtomicIntHandler;
import usingvolatile.example.VolatileHandler;

import java.util.concurrent.*;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-07 19:52
 */
public class MultiThreadTest {

    //CAS与Synchronized的性能对比
    @Test
    public void casPerformanceTest() {
        int nThreads = 128;
        CountBase counter = new SyncCounter();
        CyclicBarrier barrierStart = new CyclicBarrier(nThreads);
        CountDownLatch cdl = new CountDownLatch(nThreads);

        long begin = System.currentTimeMillis();
        for (int i = 0; i < nThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    //等待所有线程都开始后才同时跑起
                    barrierStart.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (counter.getValue() >= 100000000)
                        break;
                    else {
                        counter.increment();
                    }
                }

                cdl.countDown();
            }, "Thread-" + i);
            thread.start();
        }

        try {
            cdl.await(); //等所有线程都跑完才开始计算耗费时间
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long time = end - begin;
        System.out.println("The process is " + Thread.currentThread().getName() +
                " Value is :" + counter.getValue() + ";" + "time is:" + time);

        //计算CAS的性能
        barrierStart.reset();
        CountDownLatch casCdl = new CountDownLatch(nThreads);
        CountBase casCounter = new CasCounter();
        begin = System.currentTimeMillis();
        for (int i = 0; i < nThreads; i++) {
            Thread thread = new Thread(() -> {
                try {
                    //等待所有线程都开始后才同时跑起
                    barrierStart.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (casCounter.getValue() >= 100000000)
                        break;
                    else {
                        casCounter.increment();
                    }
                }

                casCdl.countDown();
            }, "Thread-" + i);
            thread.start();
        }

        try {
            casCdl.await(); //等所有线程都跑完才开始计算耗费时间
        } catch (Exception e) {
            e.printStackTrace();
        }
        end = System.currentTimeMillis();
        time = end - begin;
        System.out.println("The process is " + Thread.currentThread().getName() +
                " Value is :" + counter.getValue() + ";" + "time is:" + time);

    }

    //本例子说明了当前跑线程与this指针的区别。
    //线程对象可以是死的，并没有跑起来的，可以被其他活线程操作死线程的对象（this指针）
    @Test
    public void currentThreadTest() {
        CountOperate c = new CountOperate();
        c.start();
        Thread t1 = new Thread(c);
        System.out.println("main begin t1 isAlive=" + t1.isAlive());
        t1.setName("A");
        t1.start();
        System.out.println("main end t1 isAlive=" + t1.isAlive());

    }

    //使用AQS模板的自定义排他锁的使用场景，示范了线程安全和非安全的情况
    @Test
    public void customLockAQSTest() {
        CyclicBarrier barrierStart = new CyclicBarrier(30);
        CyclicBarrier barrierEnd = new CyclicBarrier(31);
        SharedResource.a = 0;
        custom.lock.aqs.example.Mutex mutex = new custom.lock.aqs.example.Mutex();

        try {
            //说明:我们启用30个线程，每个线程对i自加10000次，同步正常的话，最终结果应为300000；
            //不加锁的情况，线程不安全，资源被污染
            for (int i = 0; i < 30; i++) {
                Thread t = new Thread(() -> {
                    try {
                        //在匿名内部类中如何访问外部的变量？
                        //回答：1、使用final；2、使用全局变量
                        barrierStart.await();//等30个线程累加完毕
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 10000; j++) {
                        SharedResource.increaseUnlock();//没有同步措施的a++；
                    }
                    try {
                        barrierEnd.await();//等30个线程累加完毕
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
            }
            barrierEnd.await();
            System.out.println("线程非安全，a=" + SharedResource.a);
            //加锁的情况，线程安全
            barrierStart.reset();
            barrierEnd.reset();//重置CyclicBarrier
            SharedResource.a = 0;
            for (int i = 0; i < 30; i++) {
                new Thread(() -> {
                    try {
                        barrierStart.await();//等30个线程累加完毕
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < 10000; j++) {
                        SharedResource.increaseLock(); //a++采用Mutex进行同步处理
                    }
                    try {
                        barrierEnd.await();//等30个线程累加完毕
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            barrierEnd.await();
            System.out.println("线程安全，a=" + SharedResource.a);
        } catch (BrokenBarrierException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


    }

    //显示i++的volatile非原子性
    //AtomicInt的原子性
    @Test
    public void volatileNonAtomicTest(){
        int nTotalThread = 1000;
        ExecutorService service = Executors.newFixedThreadPool(nTotalThread);
        for (int i = 0; i < nTotalThread; i++) {
            System.out.println("创建线程" + i);
            Runnable run = new VolatileHandler();
            // 在未来某个时间执行给定的命令
            service.execute(run);
        }
        // 关闭启动线程
        service.shutdown();
        // 等待子线程结束，再继续执行下面的代码
        try{
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            System.out.println("total num is:" + VolatileHandler.getI());
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        //--------------
        service = Executors.newFixedThreadPool(nTotalThread);
        for (int i = 0; i < nTotalThread; i++) {
            System.out.println("创建线程" + i);
            Runnable run = new AtomicIntHandler();
            // 在未来某个时间执行给定的命令
            service.execute(run);
        }
        // 关闭启动线程
        service.shutdown();
        // 等待子线程结束，再继续执行下面的代码
        try{
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            System.out.println("total num is:" + AtomicIntHandler.getI());
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        System.out.println("all thread complete");
    }

    //演示多线程下共享变量的可见性
    @Test
    public void varablesAccessibleTest(){
        AccessibleHandler a = new AccessibleHandler();
        //a.runNonAccessible();
        a.runAccessible();
    }

}
