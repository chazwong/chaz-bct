package chaz.trade.input.okcoin;

import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * Created by Administrator on 2017/8/31.
 */
@ClientEndpoint
public class EndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndPoint.class);

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("Connected to server: " + session.getBasicRemote());
        try {
            SubscribeRequestModel model = new SubscribeRequestModel();
            model.setEvent("addChannel");
            model.setChannel("ok_sub_spotcny_btc_depth_20");
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
        if (jobject.has("channel")) {
            if (StringUtils.equals(jobject.get("channel").getAsString(),"ok_sub_spotcny_btc_depth_20")) {
                LOGGER.info("subscribe huibi market scueess");
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
