package serialize.example.protostuff;

import serialize.example.SerializableEntity;

/**
 * @author Lu Weijian
 * @description 由于Protostuff不使用JAVA内置那套Serializable接口，所以
 * 对transiant关键字不起作用，它也可以序列化。
 * @email lwj@kapark.cn
 * @date 2019-03-17 10:13
 */
public class ProtostuffTask implements Runnable {

    @Override
    public void run() {
        SerializableEntity entity = new SerializableEntity();
        byte[] bytes = ProtostuffUtil.serializer(entity);
        SerializableEntity objDeserializer = ProtostuffUtil.deserializer(bytes, SerializableEntity.class);
        System.out.println("nothing name is " + objDeserializer.getName());
        System.out.println("transient age is " + objDeserializer.getAge());
        System.out.println("static final mother is " + SerializableEntity.mother);
        System.out.println("static teacher is " + SerializableEntity.teacher);
        System.out.println(objDeserializer.toString());
    }
}
