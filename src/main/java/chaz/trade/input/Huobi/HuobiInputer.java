package chaz.trade.input.Huobi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Administrator on 2017/8/28.
 */
@Component
public class HuobiInputer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HuobiInputer.class);
    private final String url = "wss://api.huobi.pro/ws";

    private Session session;

    public void start() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "wss://api.huobi.com/ws";
        try {
            session = container.connectToServer(Endpoint.class, URI.create(uri));
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


}
