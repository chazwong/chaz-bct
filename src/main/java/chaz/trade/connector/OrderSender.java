package chaz.trade.connector;

import chaz.trade.model.Order;

/**
 * Created by Administrator on 2017/9/2.
 */
public interface OrderSender {
    void sendOrder(Order order);
}
