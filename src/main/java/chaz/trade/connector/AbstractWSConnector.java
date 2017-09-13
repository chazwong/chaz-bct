package chaz.trade.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Administrator on 2017/8/31.
 */
public abstract class AbstractWSConnector {
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

    protected abstract String getUrl();

    protected abstract Object getEndpointInstance();

}
