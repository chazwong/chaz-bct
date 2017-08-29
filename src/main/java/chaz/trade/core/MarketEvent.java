package chaz.trade.core;

import chaz.trade.model.MarketData;

/**
 * Created by chengzhang.wang on 2017/8/29.
 */
public class MarketEvent {
    private final MarketData marketData = new MarketData();
    private byte[] source;

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public MarketData getMarketData() {
        return marketData;
    }
}
