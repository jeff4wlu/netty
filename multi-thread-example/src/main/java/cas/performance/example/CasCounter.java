package cas.performance.example;
import java.lang.reflect.Field;
import sun.misc.Unsafe;
/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-08 10:59
 */
public class CasCounter implements CountBase{

    private volatile long value = 0;

    private static Unsafe un;
    private static long valueOffset;
    static
    {
        try{
            un = getUnsafeInstance();
            valueOffset = un.objectFieldOffset(CasCounter.class.getDeclaredField("value"));
        }catch (Exception e) {
            // TODO: handle exception
            System.out.println("init unsafe error!");
        }
    }
    @Override
    public long getValue() {
        // TODO Auto-generated method stub
        return value;
    }

    @Override
    public long increment() {
        // TODO Auto-generated method stub
        long current;
        long next;

        for(;;)
        {
            current = value;
            next = current + 1;

            if(value >= 100000000)
                return value;
            if(un.compareAndSwapLong(this, valueOffset, current, next))
                return next;
        }
    }

    private static Unsafe getUnsafeInstance() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Field theUnsafeInstance = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeInstance.setAccessible(true);
        return (Unsafe) theUnsafeInstance.get(Unsafe.class);
    }

}
