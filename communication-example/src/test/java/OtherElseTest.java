import org.junit.Test;
import serialize.example.objectinputstream.SerializeTask;
import serialize.example.protostuff.ProtostuffTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-17 9:26
 */
public class OtherElseTest {

    @Test
    public void SimpleSerializeTest(){

        (new SerializeTask()).run();
        System.out.println("----------------------------------");
        (new ProtostuffTask()).run();
    }
}
