package chaz.trade.connector.okcoin;

import chaz.trade.connector.Utils;
import chaz.trade.connector.huobi.Account;
import chaz.trade.model.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/13.
 */
public class OkcoinRestConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(OkcoinWSConnector.class);

    private final String url = "wss://real.okcoin.cn:10440/websocket/okcoinapi";

    private final String accounturl = "https://www.okcoin.cn/api/v1/userinfo.do";

    private final String orderUrl = "https://www.okcoin.cn/api/v1/trade.do";

    private final String apiKey = "09aa8c48-6a59-41a3-b643-f36e14d02be3";
    private final String secretKey = "F02198191E4169B701C770EE5CDDA712";

    private final Gson gson = new GsonBuilder().create();

    private final Account cnyAccount = new Account();

    @PostConstruct
    private void init() {
        refreshAllAccounts();
    }

    public void sendOrder(Order order) {
    }


    private final Map<String, Object> createOrder(Order order, String symbol) {
        Map<String, Object> map = new HashMap<>();
//        map.put("api_key", apiKey);
//        map.put("symbol", symbol);
//        String type = "";
//        switch (order.getOrderType()) {
//            case BID:
//                type = "buy";
//                break;
//            case ASK:
//                type = "sell";
//                break;
//        }
//        map.put("type", type);
//        map.put("price", order.getPrice());
//        map.put("amount", order.getVolume());
//        putMD5(map);
        return map;
    }

    @Scheduled(cron = "0 * * * * *")
    private void refreshAllAccounts() {
        refreshAccount("cny");
        //refreshAccount("usd");
    }

    private void refreshAccount(String market) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("api_key", apiKey);
            putMD5(map);
            Response response = ClientBuilder.newClient().target(accounturl).request(MediaType.APPLICATION_JSON).post(Entity.form(Utils.toMultivaluedHashMap(map)));
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                Map resultMap = gson.fromJson(response.readEntity(String.class), Map.class);
                if (resultMap.get("result").equals(true)) {
                    Map<String, String> asset = (Map<String, String>) ((Map<String, Map>) resultMap.get("info")).get("funds").get("asset");
                    cnyAccount.setAvailable(Double.valueOf(asset.get("net")));
                    cnyAccount.setFrozen(Double.valueOf(asset.get("total")) - Double.valueOf(asset.get("net")));
                }
            } else {
                LOGGER.error("refresh account failed: " + response.getStatusInfo().toString());
            }
        } catch (Exception e) {
            LOGGER.error("refresh account failed", e);
        }
    }

    private final void putMD5(Map<String, Object> map) {
        map.put("secret_key", secretKey);
        map.put("sign", Utils.MD5(map).toUpperCase());
        map.remove("secret_key");
    }

}
