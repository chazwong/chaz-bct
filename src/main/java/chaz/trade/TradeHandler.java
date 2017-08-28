package chaz.trade;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;

/**
 * Created by chengzhang.wang on 2017/8/26.
 */
public class TradeHandler implements EventHandler<MarketEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeHandler.class);
    private final TreeMap<Integer, MarketData> bidBook = new TreeMap<>();
    private final TreeMap<Integer, MarketData> askBook = new TreeMap<>();

    @Override
    public void onEvent(MarketEvent event, long sequence, boolean endOfBatch) throws Exception {
        if (event.getMarketData().getType() == OrderType.ASK) {
            askBook.put(event.getMarketID(), event.getMarketData());
            checkTrade();
        } else if (event.getMarketData().getType() == OrderType.BID) {
            bidBook.put(event.getMarketID(), event.getMarketData());
            checkTrade();
        } else {
            LOGGER.error("event type is neither bid nor ask");
        }
    }

    private final void checkTrade() {
        if (bidBook.lastEntry().getValue().getPrice() > askBook.firstEntry().getValue().getPrice()) {

        }
    }
}
