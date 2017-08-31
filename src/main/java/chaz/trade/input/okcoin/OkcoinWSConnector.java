package chaz.trade.input.okcoin;

import chaz.trade.input.AbstractWSConnector;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/8/31.
 */
@Component
public class OkcoinWSConnector extends AbstractWSConnector {
    private final String url = "wss://real.okcoin.cn:10440/websocket/okcoinapi";

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected Class<?> getEndpointClass() {
        return EndPoint.class;
    }
}
