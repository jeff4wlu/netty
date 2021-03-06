package main.sub.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
/**
 * @author Lu Weijian
 * @description 跑在主线程上，任务是把新连接的读写事件注册到TCPSubReactor上。
 * 每个TCPSubReactor跑在一个子线程上，子线程数为CPU核数。
 * 这些子线程一直在跑，也不在线程池中，除非主线程关掉。
 * @email lwj@kapark.cn
 * @date 2019-03-06 14:18
 */
public class Acceptor implements Runnable {

    private final ServerSocketChannel ssc; // mainReactor監聽的socket通道
    private final int cores = Runtime.getRuntime().availableProcessors(); // 取得CPU核心數
    private final Selector[] selectors = new Selector[cores]; // 創建核心數個selector給subReactor用
    private int selIdx = 0; // 當前可使用的subReactor索引
    private TCPSubReactor[] r = new TCPSubReactor[cores]; // subReactor線程
    private Thread[] t = new Thread[cores]; // subReactor線程

    public Acceptor(ServerSocketChannel ssc) throws IOException {
        this.ssc = ssc;
        // 創建多個selector以及多個subReactor線程
        for (int i = 0; i < cores; i++) {
            selectors[i] = Selector.open();
            r[i] = new TCPSubReactor(selectors[i], ssc, i);
            t[i] = new Thread(r[i]);
            t[i].start();
        }
    }

    @Override
    public synchronized void run() {
        try {
            SocketChannel sc = ssc.accept(); // 接受client連線請求
            System.out.println(sc.socket().getRemoteSocketAddress().toString()
                    + " is connected.");

            if (sc != null) {
                sc.configureBlocking(false); // 設置為非阻塞
                r[selIdx].setRestart(true); // 暫停線程
                selectors[selIdx].wakeup(); // 使一個阻塞住的selector操作立即返回
                SelectionKey sk = sc.register(selectors[selIdx],
                        SelectionKey.OP_READ); // SocketChannel向selector[selIdx]註冊一個OP_READ事件，然後返回該通道的key
                selectors[selIdx].wakeup(); // 使一個阻塞住的selector操作立即返回
                r[selIdx].setRestart(false); // 重啟線程
                sk.attach(new TCPHandler(sk, sc)); // 給定key一個附加的TCPHandler對象，此处为读事件添加处理handler
                if (++selIdx == selectors.length)
                    selIdx = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}