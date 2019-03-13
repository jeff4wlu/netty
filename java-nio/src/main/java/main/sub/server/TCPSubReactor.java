package main.sub.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
/**
 * @author Lu Weijian
 * @description 当连接的读写IO到达后，IO事件处理会跑在TCPSubReactor的线程上。
 * 实际上就是占用subreactor的资源，感觉不是真正的主从。
 * @email lwj@kapark.cn
 * @date 2019-03-06 14:19
 */
public class TCPSubReactor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;
    private boolean restart = false;
    int num;

    public TCPSubReactor(Selector selector, ServerSocketChannel ssc, int num) {
        this.ssc = ssc;
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) { // 在線程被中斷前持續運行
            //System.out.println("ID:" + num
            //      + " subReactor waiting for new event on port: "
            //      + ssc.socket().getLocalPort() + "...");
            System.out.println("waiting for restart");
            while (!Thread.interrupted() && !restart) { // 在線程被中斷前以及被指定重啟前持續運行
                try {
                    if (selector.select() == 0)
                        continue; // 若沒有事件就緒則不往下執行
                } catch (IOException e) {
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
    }

    /*
     * name: dispatch(SelectionKey key) description: 調度方法，根據事件綁定的對象開新線程
     */
    private void dispatch(SelectionKey key) {
        Runnable r = (Runnable) (key.attachment()); // 根據事件之key綁定的對象開新線程
        if (r != null)
            r.run();
    }

    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}