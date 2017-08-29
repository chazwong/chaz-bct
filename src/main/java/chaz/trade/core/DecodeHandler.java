package chaz.trade.core;

import com.lmax.disruptor.EventHandler;

/**
 * Created by chengzhang.wang on 2017/8/29.
 */
public class DecodeHandler implements EventHandler<MarketEvent> {
    @Override
    public void onEvent(MarketEvent event, long sequence, boolean endOfBatch) throws Exception {
        //TODO decode
    }
}
