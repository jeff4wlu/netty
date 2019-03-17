package rpc.version.common;

/**
 * @author Lu Weijian
 * @description RPC对外服务接口
 * @email lwj@kapark.cn
 * @date 2019-03-13 8:54
 */
public interface HelloService {
    String sayHi(String name);
    Person handlePerson(Person person);
}
