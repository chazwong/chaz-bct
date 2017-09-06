package chaz.trade.core;

import chaz.trade.model.MarketSource;
import chaz.trade.model.Order;
import chaz.trade.model.OrderType;

/**
 * Created by chengzhang.wang on 2017/8/29.
 */
public class MarketEvent extends Order {
    private OrderType orderType;
    private int level;
    private MarketSource marketID;


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public MarketSource getMarketID() {
        return marketID;
    }

    public void setMarketID(MarketSource marketID) {
        this.marketID = marketID;
    }
}
