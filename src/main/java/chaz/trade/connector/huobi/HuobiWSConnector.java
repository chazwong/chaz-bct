package chaz.trade.connector.huobi;

import chaz.trade.connector.AbstractWSConnector;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/8/28.
 */
@Component
public class HuobiWSConnector extends AbstractWSConnector{
    private final String url = "wss://api.huobi.com/ws";

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected Class<?> getEndpointClass() {
        return Endpoint.class;
    }
}
