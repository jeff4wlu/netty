import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author Lu Weijian
 * @description 备注
 * @email lwj@kapark.cn
 * @date 2019-03-05 11:50
 */
public class Utils {

    //String与ByteBuffer互转
    public static String decode(ByteBuffer bb) {
        Charset charset = Charset.forName("utf-8");
        return charset.decode(bb).toString();
    }

    public static String getReq(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        //将ByteByf信息写出到字节数组
        buf.readBytes(con);
        try {
            return new String(con, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }


}
