package chaz.trade.model;

/**
 * Created by chengzhang.wang on 2017/8/22.
 */
public class MarketData extends Order {
    private OrderType orderType;
    private int level;
    private int marketID;


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

    public int getMarketID() {
        return marketID;
    }

    public void setMarketID(int marketID) {
        this.marketID = marketID;
    }
}
