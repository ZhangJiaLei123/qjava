package blxt.qjava.utils.image;

import javax.xml.bind.DatatypeConverter;
import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片转换工具
 * @author OpenJialei
 * @date 2021年05月15日 23:54
 */
public class ImageConvertrt {

    /**
     * 本地图片转换base64
     * @param imgPath
     * @return
     */
    public static String toBase64(String imgPath) {
        byte[] data = null;
        File file = new File(imgPath);
        if(!file.exists() || file.isDirectory()){
            return null;
        }
        // 读取图片字节数组
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // 返回Base64编码过的字节数组字符串
        String base64Str = DatatypeConverter.printBase64Binary(data);
        return base64Str;
    }


}
