import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-05 9:16
 */
public class NIOServerTest {

    @Test
    public void mainSubServer(){
        try {
            main.sub.server.TCPReactor reactor = new main.sub.server.TCPReactor(8190);
            reactor.run();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void multiThreadServer(){
        try {
            multi.thread.server.TCPReactor reactor = new multi.thread.server.TCPReactor(8190);
            reactor.run();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Test
    public void singleThreadServer(){
        try {
            single.thread.server.TCPReactor reactor = new single.thread.server.TCPReactor(8190);
            reactor.run();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void nettyMultiThreadServer(){
        Runnable runnable = new NettyMultiThreadServer();
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void nonSelectableNIOServer(){
        Runnable runnable = new NonSelectableNIOServer();
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void seletableNIOServer(){
        Runnable runnable = new SelectableNIOServer();
        Thread t = new Thread(runnable);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void nioServer() {
        try {
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(8190));
            ssc.configureBlocking(false); // 设定为非阻塞
            System.out.println("server is started");

            List scList = new ArrayList<>();
            ByteBuffer buffer = ByteBuffer.allocate(48);

            while (true) {
                SocketChannel sc = ssc.accept(); // 此处不会阻塞
                if (sc != null) {
                    scList.add(sc);
                    System.out.println("new socket");
                    sc.configureBlocking(false); // 设定为非阻塞
                }
                for (Object scTemp : scList) {
                    ((SocketChannel) scTemp).read(buffer); // 此处不会阻塞
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    String tmp = Utils.decode(buffer);
                    System.out.println(tmp);
                }
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
