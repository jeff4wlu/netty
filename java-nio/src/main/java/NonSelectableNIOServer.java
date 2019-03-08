import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lu Weijian
 * @description NIO 服务器（但不是用selector），虽说是非阻塞的，但只能依靠
 * while循环不断查询是否有事件发生，浪费CPU资源
 * @email lwj@kapark.cn
 * @date 2019-03-05 11:49
 */
public class NonSelectableNIOServer implements Runnable {

    @Override
    public void run() {
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

