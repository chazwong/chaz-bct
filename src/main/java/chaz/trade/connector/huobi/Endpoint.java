package chaz.trade.connector.huobi;

import chaz.trade.Application;
import chaz.trade.core.MarketEvent;
import chaz.trade.model.MarketSource;
import chaz.trade.model.OrderType;
import com.google.gson.*;
import com.lmax.disruptor.dsl.Disruptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2017/8/30.
 */
@ClientEndpoint
@Component
public class Endpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    private final String channel = "market.btccny.depth.step0";

    private final Gson gson = new GsonBuilder().create();

    @Autowired
    private Disruptor<MarketEvent> disruptor;

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Connected to server: " + session.getBasicRemote());
        try {
            SubscribeRequestModel model = new SubscribeRequestModel();
            model.setId(String.valueOf(MarketID.MARKET_SUBSCRIBE.ordinal()));
            model.setSub(channel);
            session.getBasicRemote().sendText(gson.toJson(model));
        } catch (IOException ex) {
            LOGGER.error("got exception", ex);
        }
    }

    @OnMessage
    public void processMessage(String message) {
        JsonElement jelement = new JsonParser().parse(message);
        JsonObject jobject = jelement.getAsJsonObject();
        if (jobject.has("id")) {
            if (StringUtils.equals(jobject.get("id").getAsString(), String.valueOf(MarketID.MARKET_SUBSCRIBE.ordinal())) && StringUtils.equals(jobject.get("status").getAsString(), "ok")) {
                LOGGER.info("subscribe huibi market scueess");
            }
        } else if (jobject.has("ch")) {
            if (StringUtils.equals(jobject.get("ch").getAsString(), channel)) {
                JsonElement bids = jobject.getAsJsonArray("data").get(0);
                JsonElement asks = jobject.getAsJsonArray("data").get(1);
            }
        }
    }

    @OnMessage
    public void processBinary(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        String msgStr = out.toString();
        LOGGER.info("received from huobi:{}", msgStr);
        Map<String, Object> map = gson.fromJson(msgStr, Map.class);
        if (map.containsKey("id") && map.get("status").equals("ok") && map.get("subbed").equals(channel)) {
            LOGGER.info("subscribe {} succeed", channel);
        } else if (map.containsKey("ch") && map.get("ch").equals(channel)) {
            Map<String, Object> tickMap = (Map<String, Object>) map.get("tick");
            List<List<Double>> bids = (List<List<Double>>) tickMap.get("bids");
            List<List<Double>> asks = (List<List<Double>>) tickMap.get("asks");
            disruptor.publishEvent((event, sequence, arg) -> {
                event.setOrderType(OrderType.BID);
                event.setPrice(arg.get(0));
                event.setVolume(arg.get(1));
                event.setLevel(0);
                event.setMarketID(MarketSource.HUOBI);
            }, bids.get(0));
            disruptor.publishEvent((event, sequence, arg) -> {
                event.setOrderType(OrderType.ASK);
                event.setPrice(arg.get(0));
                event.setVolume(arg.get(1));
                event.setLevel(0);
                event.setMarketID(MarketSource.HUOBI);
            }, bids.get(1));
        } else if (map.containsKey("ping")) {
            // do nothing
        } else {
            LOGGER.warn("unknown message");
        }
    }

    @OnError
    public void processError(Throwable t) {
        LOGGER.error("got error from server", t);
    }
}
