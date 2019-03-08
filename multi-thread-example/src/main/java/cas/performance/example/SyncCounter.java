package cas.performance.example;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-08 10:24
 */
public class SyncCounter implements CountBase{
    private volatile long value = 0;

    @Override
    public synchronized long getValue() {
        // TODO Auto-generated method stub
        return value;
    }

    @Override
    public synchronized long increment() {
        // TODO Auto-generated method stub
        if (value <= 100000000)
            return ++value;
        else
            return value;
    }

}