package chaz.trade.input.Huibi;

import chaz.trade.core.MarketEvent;
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2017/8/30.
 */
@Component
@ClientEndpoint
public class Endpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(Endpoint.class);

    @Autowired
    private Disruptor<MarketEvent> disruptor;

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Connected to server: " + session.getBasicRemote());
        try {
            SubRequestModel model = new SubRequestModel();
            model.setId(String.valueOf(MarketID.MARKET_SUBSCRIBE.ordinal()));
            model.setSub("market.btccny.depth.step1");
            Gson gson = new GsonBuilder().create();
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
            if (StringUtils.equals(jobject.get("ch").getAsString(), "market.btccny.depth.step0")) {
                JsonElement bids = jobject.getAsJsonArray("data").get(0);
                JsonElement asks = jobject.getAsJsonArray("data").get(1);
            }
        }
    }

    @OnMessage
    public void processBinary(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(new String(bytes, StandardCharsets.ISO_8859_1).getBytes("ISO-8859-1"));
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        LOGGER.info("received bytes {}",out.toString());
    }

    @OnError
    public void processError(Throwable t) {
        LOGGER.error("got error from server", t);
    }
}
