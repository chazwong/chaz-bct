package chaz.trade;

/**
 * Created by chengzhang.wang on 2017/8/26.
 */
public class MarketEvent {
    private int marketID;
    private MarketData marketData;


    public int getMarketID() {
        return marketID;
    }

    public void setMarketID(int marketID) {
        this.marketID = marketID;
    }

    public MarketData getMarketData() {
        return marketData;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }
}
