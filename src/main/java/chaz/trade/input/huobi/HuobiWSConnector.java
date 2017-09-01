package chaz.trade.input.huobi;

import chaz.trade.input.AbstractWSConnector;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/8/28.
 */
@Component
public class HuobiWSConnector extends AbstractWSConnector{
    private final String url = "wss://api.huobi.pro/ws";

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected Class<?> getEndpointClass() {
        return Endpoint.class;
    }
}
