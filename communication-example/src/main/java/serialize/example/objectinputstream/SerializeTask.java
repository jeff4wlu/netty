package serialize.example.objectinputstream;

import serialize.example.SerializableEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lu Weijian
 * @description Java内置序列化的三种方式(之一)
 * static字段是类共享的字段，当该类的一个对象被序列化
 * 后，这个static变量可能会被另一个对象改变，所以这就决定了静态变量是
 * 不能序列化的，但如果再加上final修饰，就可以被序列化了，因为这是一个
 * 常量，不会改变。
 * @email lwj@kapark.cn
 * @date 2019-03-17 9:11
 */
public class SerializeTask implements Runnable {

    @Override
    public void run(){
        SerializableEntity entity = new SerializableEntity();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("D:\\serialTxt")));
            oos.writeObject(entity);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("D:\\serialTxt")));
            SerializableEntity objRead = (SerializableEntity) ois.readObject();
            System.out.println("nothing name is " + objRead.getName());
            System.out.println("transient age is " + objRead.getAge());
            System.out.println("static final mother is " + SerializableEntity.mother);
            System.out.println("static teacher is " + SerializableEntity.teacher);
            System.out.println(objRead.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }



}
