package chaz.trade.connector.okcoin;

import chaz.trade.connector.Utils;
import com.google.gson.Gson;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private static final String bidType = "buy";
    private static final String askType = "sell";

    private final double limitMoney = 2000;


    private final Map<String, Consumer<Map>> consumerMap = new HashMap<>();

    private Map<String, Double> free = new HashMap<>();
    private Map<String, Double> freezed = new HashMap<>();

    private Session session;

    private final String btcChannel = getChannel("btc");

    private final String ltcChannel = getChannel("ltc");

    private final String ethChannel = getChannel("eth");

    private final String etcChannel = getChannel("etc");

    private final String accountChannel = "ok_sub_spotcny_userinfo";

    private final String accountQueryChannel = "ok_spotcny_userinfo";

    private final String tradeChannel = "ok_spotcny_trade";

    private final String tradesChannel = "ok_sub_spotcny_trades";

    public EndPoint() {
        consumerMap.put(accountChannel, (map) -> {
            Map info = (Map) map.get("info");
            free = (Map<String, Double>) info.get("free");
            freezed = (Map<String, Double>) info.get("freezed");
        });
        consumerMap.put(accountQueryChannel, (map) -> {
            Map<String, Map<String, String>> funds = (Map<String, Map<String, String>>) ((Map) map.get("info")).get("funds");
            funds.get("free").entrySet().forEach((entry) -> {
                free.put(entry.getKey(), Double.valueOf(entry.getValue()));
            });
            funds.get("freezed").entrySet().forEach((entry) -> {
                freezed.put(entry.getKey(), Double.valueOf(entry.getValue()));
            });
        });
        consumerMap.put(ltcChannel, (map) -> {
            doCaculate(map, 0.1f, "ltc", "ltc_cny");
        });

        consumerMap.put(ethChannel, (map) -> {
            doCaculate(map, 0.01f, "eth", "eth_cny");
        });

        consumerMap.put(etcChannel, (map) -> {
            doCaculate(map, 0.01f, "etc", "etc_cny");
        });

        consumerMap.put(btcChannel, (map) -> {
            doCaculate(map, 0.01f, "btc", "btc_cny");
        });
        consumerMap.put(tradeChannel, (map) -> {
            if ((Boolean) map.get("result")) {
                LOGGER.info("add order success {}", map.get("order_id"));
            } else {
                LOGGER.error("add order failed {}", map.get("order_id"));
            }
        });
        consumerMap.put(tradesChannel, (map) -> {
            LOGGER.info("got trades:", gson.toJson(map));
        });

    }

    private final void doCaculate(Map map, double least, String freeKey, String symbol) {
        List<List<String>> asks = (List<List<String>>) map.get("asks");
        List<List<String>> bids = (List<List<String>>) map.get("bids");
        List<String> firstBidPair = bids.get(0);
        List<String> firstAskPair = asks.get(asks.size() - 1);
        double firstBidPrice = Double.valueOf(firstBidPair.get(0));
        double firstBidVolume = Double.valueOf(firstBidPair.get(1));
        double firstAskPrice = Double.valueOf(firstAskPair.get(0));
        double firstAskVolume = Double.valueOf(firstAskPair.get(1));
        double bidPrice = firstBidPrice + 0.01f; //取最高买价
        double askPrice = firstAskPrice - 0.01f; //取卖价卖价
        if (hasProfit(bidPrice, askPrice)) {
            double cnyFree = free.getOrDefault("cny", 0.0d) - 500d;
            double ltcBalance = free.get(freeKey);
            if (ltcBalance < least && cnyFree >= askPrice * least) {//无币有钱，买币
                sendOrder(bidPrice, cnyFree / bidPrice, bidType, symbol);
            } else if (ltcBalance >= least && cnyFree < askPrice * least) {//有币无钱，卖币
                sendOrder(askPrice, ltcBalance, askType, symbol);
            } else if (ltcBalance >= least && cnyFree >= askPrice * least) {//有币有钱,卖币买币
                double volume = ObjectUtils.min(cnyFree / bidPrice, ltcBalance);
                sendOrder(askPrice, volume, askType, symbol);
                sendOrder(bidPrice, volume, bidType, symbol);
            } else {
                LOGGER.error("no enough money and coins for {}", symbol);
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("******Connected to okcoin******");
        this.session = session;
        sendRequest("login", null, null);
        sendRequest("addChannel", accountQueryChannel, null);
        //subscribeMarket(etcChannel);
        subscribeMarket(ltcChannel);
        subscribeMarket(ethChannel);
        subscribeMarket(btcChannel);
    }

    //每分钟登陆一次
    @Scheduled(cron = "0 * * * * *")
    private void login() {
        sendRequest("login", null, null);
    }

    @OnMessage
    public void processMessage(String message) {
        LOGGER.info("received from okcoin:{}", message);
        Map[] maps = gson.fromJson(message, Map[].class);
        for (Map map : maps) {
            if (StringUtils.equals("login", (CharSequence) map.get("channel"))) {
                Map<String, Object> data = (Map<String, Object>) map.get("data");
                if (data.get("result").equals(true) || data.get("result").equals("true")) {
                    LOGGER.info("******login success******");
                } else {
                    LOGGER.error("******login failed******");
                }
            } else if (StringUtils.equals("addChannel", (CharSequence) map.get("channel"))) {
                Map<String, Object> data = (Map<String, Object>) map.get("data");
                if ((Boolean) data.get("result")) {
                    LOGGER.info("******add channel {} success******", data.get("channel"));
                } else {
                    LOGGER.error("******add channel {} failed, error code {}******", data.get("channel"), data.get("error_code"));
                }
            } else {
                consumerMap.get(map.get("channel")).accept((Map) map.get("data"));
            }
        }
    }

    //计算是否有套利空间
    private final boolean hasProfit(double bidPrice, double askPrice) {
        return (askPrice - bidPrice - (askPrice + bidPrice) * FEE) > 0;
    }

    private final String getChannel(String type) {
        return String.format("ok_sub_spotcny_%s_depth_%s", type, DEPTH);
    }

    private final void subscribeMarket(String channel) {
        Map model = new HashMap();
        model.put("event", "addChannel");
        model.put("channel", channel);
        try {
            session.getBasicRemote().sendText(gson.toJson(model));
        } catch (IOException e) {
            LOGGER.error("subscribe channel {} failed", channel, e);
        }
    }

    private final void sendRequest(String event, String channel, Map map) {
        Map parameters = new HashMap();
        parameters.put("api_key", API_KEY);
        if (null != map) {
            parameters.putAll(map);
        }
        putMD5(parameters);
        Map model = new HashMap();
        model.put("event", event);
        if (null != channel) {
            model.put("channel", channel);
        }
        model.put("parameters", parameters);
        try {
            session.getBasicRemote().sendText(gson.toJson(model));
        } catch (IOException e) {
            LOGGER.error("subscribe channel {} failed", ltcChannel, e);
        }
    }


    private final void sendOrder(double price, double volume, String orderType, String symbol) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("api_key", API_KEY);
        parameters.put("symbol", symbol);
        parameters.put("type", orderType);
        parameters.put("price", String.valueOf(price));
        parameters.put("amount", String.valueOf(volume));
        putMD5(parameters);
        Map<String, Object> order = new HashMap<>();
        order.put("event", "addChannel");
        order.put("channel", tradeChannel);
        order.put("parameters", parameters);
        try {
            String orderJason = gson.toJson(order);
            session.getBasicRemote().sendText(orderJason);
            LOGGER.info("******send order success: {}******", orderJason);
        } catch (IOException e) {
            LOGGER.error("send order failed for exception", e);
        }
    }


    private final void putMD5(Map<String, Object> map) {
        String toSignStr = map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue().toString())).collect(Collectors.joining("&"));
        map.put("sign", Utils.MD5(String.format("%s&secret_key=%s", toSignStr, SECRET_KEY)).toUpperCase());
    }

}
