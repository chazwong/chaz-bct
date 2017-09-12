package chaz.trade.connector.okcoin;

import chaz.trade.connector.Utils;
import chaz.trade.model.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/31.
 */
@ClientEndpoint
@Component
public class EndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndPoint.class);
    private static final double FEE = 0.002d;
    private static final int DEPTH = 5;
    private static final String API_KEY = "09aa8c48-6a59-41a3-b643-f36e14d02be3";
    private static final String SECRET_KEY = "F02198191E4169B701C770EE5CDDA712";
    private static final Gson gson = new Gson();
    private float cnyBalance = 0.0f;
    private float ltcBalance = 0.0f;
    private float btcBlance = 0.0f;
    private float ethBlance = 0.0f;


    private final String btcChannel = getChannel("btc");

    private final String ltcChannel = getChannel("ltc");

    private final String ethChannel = getChannel("eth");

    private static final String getChannel(String type) {
        return String.format("ok_sub_spotcny_%s_depth_%s", type, DEPTH);
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Connected to server: " + session.getBasicRemote());
        try {
            SubscribeRequestModel model = new SubscribeRequestModel();
            model.setEvent("addChannel");
            model.setChannel(ltcChannel);
            Gson gson = new GsonBuilder().create();
            session.getBasicRemote().sendText(gson.toJson(model));
        } catch (IOException ex) {
            LOGGER.error("got exception", ex);
        }
    }

    @OnMessage
    public void processMessage(String message) {
        LOGGER.info("received from okcoin:{}", message);
        Map first = gson.fromJson(message, Map[].class)[0];
        if (StringUtils.equals((String) first.get("channel"), ltcChannel)) {
            Map<String, List<List<String>>> map = (Map<String, List<List<String>>>) first.get("data");
            List<List<String>> asks = map.get("asks");
            List<List<String>> bids = map.get("bids");
            List<String> firstBidPair = bids.get(0);
            List<String> firstAskPair = asks.get(asks.size() - 1);
            float firstBidPrice = Float.valueOf(firstBidPair.get(0));
            float firstBidVolume = Float.valueOf(firstBidPair.get(1));
            float firstAskPrice = Float.valueOf(firstAskPair.get(0));
            float firstAskVolume = Float.valueOf(firstAskPair.get(1));
            float bidPrice = firstBidPrice - 0.01f;
            float askPrice = firstAskPrice - 0.01f;
            if (hasProfit(bidPrice, askPrice)) {
                float can
                float volume = ObjectUtils.min(cnyBalance / bidPrice, ltcBalance,);
                if(0==volume){

                }else {

                }
            }
        } else if (StringUtils.equals((String) first.get("channel"), btcChannel)) {
            Map<String, List<List<String>>> map = (Map<String, List<List<String>>>) first.get("data");
            List<List<String>> asks = map.get("asks");
            List<List<String>> bids = map.get("bids");

        }
    }

    private final boolean hasProfit(float bidPrice, float askPrice) {
        return (askPrice - bidPrice - (askPrice + bidPrice) * FEE) > 0;
    }

    private final Map<String, Object> createOrder(Order order, String symbol) {
        Map<String, Object> map = new HashMap<>();
        map.put("api_key", API_KEY);
        map.put("symbol", symbol);
        String type = "";
        switch (order.getOrderType()) {
            case BID:
                type = "buy";
                break;
            case ASK:
                type = "sell";
                break;
        }
        map.put("type", type);
        map.put("price", order.getPrice());
        map.put("amount", order.getVolume());
        putMD5(map);
        return map;
    }


    private final void putMD5(Map<String, Object> map) {
        map.put("secret_key", SECRET_KEY);
        map.put("sign", Utils.MD5(map).toUpperCase());
        map.remove("secret_key");
    }

    @OnError
    public void onError(Throwable thr) {
        LOGGER.error("got error from server", thr);
    }

}
