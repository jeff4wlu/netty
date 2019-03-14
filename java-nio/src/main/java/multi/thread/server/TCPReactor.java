package multi.thread.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Lu Weijian
 * @description JAVA NIO的多线程reactor模式
 * 跑在主线程上的监听端口，处理accept事件
 * accept、read、write事件都是由同一个selector监听，并且其IO事件处理都跑在主线程上。
 * 但解包后的业务处理是放在线程池上跑。
 * 线程模型, 1：M
 * @email lwj@kapark.cn
 * @date 2019-03-06 14:08
 */
public class TCPReactor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public TCPReactor(int port) throws IOException {
        selector = Selector.open();
        ssc = ServerSocketChannel.open();
        InetSocketAddress addr = new InetSocketAddress(port);
        ssc.socket().bind(addr); // 在ServerSocketChannel綁定監聽端口
        ssc.configureBlocking(false); // 設置ServerSocketChannel為非阻塞
        SelectionKey sk = ssc.register(selector, SelectionKey.OP_ACCEPT);
        // 把同一个selector传给acceptor，以便其注册读事件，所以accept与read是同一个selector监听
        sk.attach(new Acceptor(selector, ssc));
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) { // 在線程被中斷前持續運行
            System.out.println("Waiting for new event on port: " + ssc.socket().getLocalPort() + "...");
            try {
                if (selector.select() == 0) // 若沒有事件就緒則不往下執行
                    continue;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Set<SelectionKey> selectedKeys = selector.selectedKeys(); // 取得所有已就緒事件的key集合
            Iterator<SelectionKey> it = selectedKeys.iterator();
            while (it.hasNext()) {
                dispatch((SelectionKey) (it.next())); // 根據事件的key進行調度
                it.remove();
            }
        }
    }

    /*
     * name: dispatch(SelectionKey key)
     * description: 調度方法，根據事件綁定的對象開新線程
     */
    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment()); // 根據事件之key綁定的對象開新線程
        if (r != null)
            r.run();
    }

}
