import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * @author Lu Weijian
 * @description seletable NIO server对比起BIO服务器高性能在
 * 于其单线程可以处理多个channel(socket)，处理多个事件（socket
 * 连接接入、读写IO），并不占用CPU时间。是单线程的reactor模型。
 * @email lwj@kapark.cn
 * @date 2019-03-05 11:45
 */
public class SelectableNIOServer implements Runnable {

    private static void handle(Selector selector, SelectionKey key){
        try{
            if(key.isAcceptable()) {
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel sc = channel.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);//注册读事件
                SelectableNIOServer.map.put(sc, ByteBuffer.allocate(48));//把socket和handle进行绑定
                System.out.println("new socket");
            }

            if(key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = (ByteBuffer)map.get(sc);
                sc.read(buffer); // 此处不会阻塞
                buffer.flip();
                while (buffer.hasRemaining()) {
                    String tmp = Utils.decode(buffer);
                    System.out.println(tmp);
                }
                buffer.clear();

            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.bind(new InetSocketAddress(8190));
            ssc.configureBlocking(false); // 设定为非阻塞
            //注册到选择器上，设置为监听状态
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server is listening now...");

            List scList = new ArrayList<>();
            ByteBuffer buffer = ByteBuffer.allocate(48);

            while (true) {
                int selected = selector.select(); //此处阻塞等待

                if (selected > 0) {
                    Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                    while(selectedKeys.hasNext()) {
                        SelectionKey key = selectedKeys.next();
                        if(key.isValid())
                            SelectableNIOServer.handle(selector, key);
                        selectedKeys.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map map = new HashMap<>();

}
