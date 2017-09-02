package chaz.trade.connector;

import chaz.trade.model.Order;

/**
 * Created by Administrator on 2017/9/2.
 */
public interface OrderSender {
    void sendBidOrder(Order order);
    void sendAskOrder(Order order);
}
