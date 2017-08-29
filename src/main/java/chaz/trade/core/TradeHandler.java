package chaz.trade.core;

import chaz.trade.model.MarketData;
import chaz.trade.model.Order;
import chaz.trade.model.OrderType;
import chaz.trade.output.OrderSender;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by chengzhang.wang on 2017/8/26.
 */

public class TradeHandler implements EventHandler<MarketEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeHandler.class);
    private final TreeMap<Integer, MarketData> bidBook = new TreeMap<>();
    private final TreeMap<Integer, MarketData> askBook = new TreeMap<>();
    private final Map<Integer, OrderSender> orderSenderMap = new HashMap<>();

    @Override
    public void onEvent(MarketEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getMarketData().getOrderType() == OrderType.ASK) {
            askBook.put(event.getMarketData().getMarketID(), event.getMarketData());
            checkTrade();
        } else if (event.getMarketData().getOrderType() == OrderType.BID) {
            bidBook.put(event.getMarketData().getMarketID(), event.getMarketData());
            checkTrade();
        } else {
            LOGGER.error("event type is neither bid nor ask");
        }
    }

    private final void checkTrade() {
        MarketData firstBid = bidBook.lastEntry().getValue();
        MarketData firstAsk = askBook.firstEntry().getValue();
        if ((firstBid.getPrice() - firstAsk.getPrice()) > 100) {
            Order bidOrder = new Order();
            bidOrder.setPrice(firstAsk.getPrice());
            bidOrder.setVolume(Math.min(firstBid.getVolume(), firstAsk.getVolume()));
            orderSenderMap.get(firstAsk.getMarketID()).sendBidOrder(bidOrder);
            Order askOrder = new Order();
            askOrder.setPrice(firstBid.getPrice());
            askOrder.setVolume(Math.min(firstBid.getVolume(), firstAsk.getVolume()));
        }
    }
}
