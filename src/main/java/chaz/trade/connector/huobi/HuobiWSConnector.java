package chaz.trade.connector.huobi;

import chaz.trade.connector.AbstractWSConnector;
import chaz.trade.model.*;
import chaz.trade.model.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/28.
 */
@Component
public class HuobiWSConnector extends AbstractWSConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(HuobiWSConnector.class);

    private final String url = "wss://api.huobi.com/ws";

    private final Gson gson = new GsonBuilder().create();

    private final Map<MarketType, Account> accounts = new HashMap<>();

    private final String access_key = "6a674ef0-7468c57c-eff323dd-c9441";
    private final String secret_key = "b4167183-2fd35090-a1b96186-7c4f4";


    @Autowired
    private Endpoint endpoint;

    @Override
    protected String getUrl() {
        return url;
    }

    @Override
    protected Object getEndpointInstance() {
        return endpoint;
    }

    @Override
    public void sendOrder(Order order) {
        try {
            session.getBasicRemote().sendText(gson.toJson(createOrder(order)));
        } catch (Exception e) {
            LOGGER.error("send huobi order failed", e);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    private void refreshAllAccounts() {
        refreshAccount("cny");
        //refreshAccount("usd");
    }

    private void refreshAccount(String market) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("method", "get_account_info");
            map.put("access_key", access_key);
            map.put("created", System.currentTimeMillis());
            map.put("sign", MessageDigest.getInstance("MD5").digest(String.format("access_key=%s&created=%s&method=get_account_info&secret_key=%s", access_key, map.get("created"), "get_account_info", secret_key).getBytes()));
            map.put("market", "get_account_info");
            map.put("market", market);
            session.getBasicRemote().sendText(gson.toJson(map));
        } catch (Exception e) {
            LOGGER.error("refresh account failed", e);
        }
    }


    private final Map<String, Object> createOrder(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("method", getMethod(order.getOrderType()));
        map.put("access_key", access_key);
        map.put("coin_type", getCoinType(order.getMarketType()));
        map.put("price", order.getPrice());
        map.put("amount", order.getVolume());
        map.put("created", System.currentTimeMillis());
        try {
            map.put("sign", MessageDigest.getInstance("MD5").digest(String.format
                    ("access_key=%s&amount=%s&coin_type=%s&created=%s&method=%s&price=%s&secret_key=%s", access_key, order.getVolume(), map.get("coin_type"), map.get("created"), map.get("method"), order.getPrice(), secret_key).getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        map.put("trade_password", "wcz19891124097X");
        map.put("trade_id", 111);
        map.put("market", "cny");
        return map;
    }

    private final String getMethod(chaz.trade.model.OrderType orderType) {
        switch (orderType) {
            case ASK:
                return "sell";
            case BID:
                return "buy";
            default:
                throw new RuntimeException("unknown type" + orderType.name());
        }
    }

    private final int getCoinType(MarketType marketType) {
        switch (marketType) {
            case BTC:
                return 1;
            case LTC:
                return 2;
            default:
                throw new RuntimeException("unsupported coin type" + marketType.name());
        }
    }
}
