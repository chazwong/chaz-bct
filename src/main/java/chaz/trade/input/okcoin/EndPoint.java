package chaz.trade.input.okcoin;

import chaz.trade.Application;
import chaz.trade.core.MarketEvent;
import chaz.trade.model.MarketSource;
import chaz.trade.model.OrderType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lmax.disruptor.dsl.Disruptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/31.
 */
@ClientEndpoint
public class EndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndPoint.class);
    private final Gson gson = new Gson();

    private final String channel = "ok_sub_spot_btc_depth";

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Connected to server: " + session.getBasicRemote());
        try {
            SubscribeRequestModel model = new SubscribeRequestModel();
            model.setEvent("addChannel");
            model.setChannel(channel);
            Gson gson = new GsonBuilder().create();
            session.getBasicRemote().sendText(gson.toJson(model));
        } catch (IOException ex) {
            LOGGER.error("got exception", ex);
        }
    }

    @OnMessage
    public void processMessage(String message) {
        LOGGER.info("received from okcin:{}", message);
        Map first = gson.fromJson(message, Map[].class)[0];
        if (StringUtils.equals((String) first.get("channel"), "addChannel")) {
            Map<String, Object> map = (Map<String, Object>) first.get("data");
            if ((Boolean) map.get("result")) {
                LOGGER.info("subscribe {} succeed", channel);
            } else {
                LOGGER.info("subscribe {} failed", channel);
            }
        } else if (StringUtils.equals((String) first.get("channel"), channel)) {
            Map<String, List<List<String>>> map = (Map<String, List<List<String>>>) first.get("data");
            List<List<String>> asks = map.get("asks");
            List<List<String>> bids = map.get("bids");
            if (asks.size() > 0) {
                Application.getDisuptor().publishEvent((event, sequence, arg) -> {
                    event.setLevel(0);
                    event.setMarketID(MarketSource.OKCOIN);
                    event.setPrice(Float.valueOf(arg.get(0)));
                    event.setVolume(Float.valueOf(arg.get(1)));
                    event.setOrderType(OrderType.ASK);
                }, asks.get(0));
            }
            if (bids.size() > 0) {
                Application.getDisuptor().publishEvent((event, sequence, arg) -> {
                    event.setLevel(0);
                    event.setMarketID(MarketSource.OKCOIN);
                    event.setPrice(Float.valueOf(arg.get(0)));
                    event.setVolume(Float.valueOf(arg.get(1)));
                    event.setOrderType(OrderType.BID);
                }, bids.get(0));
            }
        }
    }

    @OnError
    public void onError(Throwable thr) {
        LOGGER.error("got error from server", thr);
    }

}
