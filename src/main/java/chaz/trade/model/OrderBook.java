package chaz.trade.model;

import java.util.TreeMap;

/**
 * Created by chengzhang.wang on 2017/8/22.
 */
public class OrderBook {
    private final TreeMap<String,MarketData> bidBook = new TreeMap<>();
    private final TreeMap<String,MarketData> askBook = new TreeMap<>();
}
