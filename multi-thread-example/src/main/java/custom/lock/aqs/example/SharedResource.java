package custom.lock.aqs.example;


/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-07 20:41
 */
public class SharedResource {
    public static int a = 0;

    private static custom.lock.aqs.example.Mutex mutex = new custom.lock.aqs.example.Mutex();

    public static void increaseUnlock(){
        a++;
    }

    public static void increaseLock(){
        mutex.lock();
        a++;
        mutex.unlock();
    }
}
