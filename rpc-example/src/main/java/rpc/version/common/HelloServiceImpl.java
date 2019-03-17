package rpc.version.common;


/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-13 8:55
 */
public class HelloServiceImpl implements HelloService {

    public String sayHi(String name) {
        return "Hi, " + name;
    }

    public Person handlePerson(Person person) {
        Person tmp = null;
        if (person != null) {
            tmp = new Person();
            tmp.setPhone(person.getPhone());
            tmp.setAge(person.getAge() + 10);
            tmp.setName(person.getName() + " GRPC");
        }
        return tmp;
    }

}
