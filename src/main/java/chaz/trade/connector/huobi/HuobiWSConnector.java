package chaz.trade.connector.huobi;

import chaz.trade.connector.AbstractWSConnector;
import chaz.trade.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/8/28.
 */
@Component
public class HuobiWSConnector extends AbstractWSConnector{
    private final String url = "wss://api.huobi.com/ws";

    @Autowired
    private Endpoint endpoint;

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected Object getEndpointInstance() {
        return endpoint;
    }

    @Override
    public void sendBidOrder(Order order) {

    }

    @Override
    public void sendAskOrder(Order order) {

    }
}
