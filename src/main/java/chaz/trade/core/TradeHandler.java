package chaz.trade.core;

import chaz.trade.connector.OrderSender;
import chaz.trade.model.MarketSource;
import chaz.trade.model.MarketType;
import chaz.trade.model.Order;
import chaz.trade.model.OrderType;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by chengzhang.wang on 2017/8/26.
 */
@Component
public class TradeHandler implements EventHandler<MarketEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeHandler.class);
    private final TreeSet<MarketEvent> bidBook = new TreeSet<>((e1, e2) -> (int) (e2.getPrice() - e1.getPrice()));
    private final TreeSet<MarketEvent> askBook = new TreeSet<>((e1, e2) -> (int) (e1.getPrice() - e2.getPrice()));
    private final Map<MarketSource, OrderSender> orderSenderMap = new HashMap<>();
    private final Map<MarketSource, Double> balances = new HashMap<>();

    @Override
    public void onEvent(MarketEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getOrderType() == OrderType.ASK) {
            askBook.add(event);
            checkTrade();
        } else if (event.getOrderType() == OrderType.BID) {
            bidBook.add(event);
            checkTrade();
        } else {
            LOGGER.error("event type is neither bid nor ask");
        }
    }

    private final void checkTrade() {
        if (bidBook.size() > 0 && askBook.size() > 0) {
            MarketEvent firstBid = bidBook.first();
            MarketEvent firstAsk = askBook.first();
            if ((firstBid.getPrice() - firstAsk.getPrice()) > 100) {
                Order bidOrder = new Order();
                bidOrder.setPrice(firstAsk.getPrice());
                bidOrder.setVolume(Math.min(firstBid.getVolume(), firstAsk.getVolume()));
                bidOrder.setMarketType(MarketType.BTC);
                bidOrder.setOrderType(OrderType.BID);
                orderSenderMap.get(firstAsk.getMarketID()).sendOrder(bidOrder);
                Order askOrder = new Order();
                askOrder.setPrice(firstBid.getPrice());
                askOrder.setVolume(Math.min(firstBid.getVolume(), firstAsk.getVolume()));
                askOrder.setMarketType(MarketType.BTC);
                askOrder.setOrderType(OrderType.BID);
                orderSenderMap.get(firstBid.getMarketID()).sendOrder(askOrder);
            }
        }
    }
}
