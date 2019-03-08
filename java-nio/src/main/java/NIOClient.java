import io.netty.channel.nio.NioEventLoop;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-05 10:14
 */
public class NIOClient {
    public static void main(String[] args) {
        try {
            //1. 获取通道
            SocketChannel clientSoc = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8190));
            //2. 切换非阻塞模式
            //clientSoc.configureBlocking(false);
            //3. 分配指定大小的缓冲区
            ByteBuffer allocate = ByteBuffer.allocate(1024);
            //4. 发送数据给服务端
            System.out.println("pls enter sth");
            Scanner sc = new Scanner(System.in);
            while (sc.hasNext()) {
                String next = sc.next();
                allocate.put((LocalDate.now() + "--客户端说" + "：" + next).getBytes());
                allocate.flip();
                clientSoc.write(allocate);
                allocate.clear();
            }
            sc.close();
            //5. 关闭通道
            clientSoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
