package usingvolatile.example;

/**
 * @author Lu Weijian
 * @description 演示共享变量的可见性
 * @email lwj@kapark.cn
 * @date 2019-03-12 15:09
 */
public class AccessibleHandler {

    //类成员变量放在heap上，多线程下其他线程不一定可见
    public static boolean bStop = false;

    //volatile使用了内存屏障技术，可使其他线程可见。
    public static volatile boolean bVStop = false;

    public void runNonAccessible() {
        //普通情况下，多线程不能保证可见性
        new Thread(() -> {
            System.out.println("Ordinary A is running...");
            while (!AccessibleHandler.bStop) ;
            System.out.println("Ordinary A is terminated.");
        }).start();
        try{
            Thread.sleep(10);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        new Thread(() -> {
            System.out.println("Ordinary B is running...");
            AccessibleHandler.bStop = true;
            System.out.println("Ordinary B is terminated.");
        }).start();

    }

    //使用volatile后其他线程可见
    public void runAccessible() {
        new Thread(() -> {
            System.out.println("Ordinary A is running...");
            while (!AccessibleHandler.bVStop) ;
            System.out.println("Ordinary A is terminated.");
        }).start();
        try{
            Thread.sleep(10);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        new Thread(() -> {
            System.out.println("Ordinary B is running...");
            AccessibleHandler.bVStop = true;
            System.out.println("Ordinary B is terminated.");
        }).start();
    }
}
