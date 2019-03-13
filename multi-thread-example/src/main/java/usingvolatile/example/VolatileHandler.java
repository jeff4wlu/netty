package usingvolatile.example;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-12 12:50
 */
public class VolatileHandler implements Runnable {

    static volatile int i = 0;

    public static void addI(){
        i++;
    }
    public static int getI(){
        return i;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        VolatileHandler.addI();

    }
}
