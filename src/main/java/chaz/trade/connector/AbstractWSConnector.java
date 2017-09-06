package chaz.trade.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/8/31.
 */
public abstract class AbstractWSConnector implements OrderSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWSConnector.class);

    protected Session session;

    public void start() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            session = container.connectToServer(getEndpointInstance(), URI.create(getUrl()));
        } catch (Exception e) {
            LOGGER.error("connect server failed", e);
        }
    }

    public void stop() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                LOGGER.error("close session failed", e);
            }
        }
    }

    protected final String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = s.getBytes("utf-8");
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected abstract String getUrl();

    protected abstract Object getEndpointInstance();

}
