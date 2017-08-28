package chaz.trade.output;

import chaz.trade.model.Order;

/**
 * Created by Administrator on 2017/8/28.
 */
public interface OrderSender {
    void sendBidOrder(Order order);
    void sendAskOrder(Order order);
}
