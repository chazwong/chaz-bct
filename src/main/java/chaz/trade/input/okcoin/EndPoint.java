package chaz.trade.input.okcoin;

import chaz.trade.core.MarketEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.lmax.disruptor.dsl.Disruptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/31.
 */
@ClientEndpoint
@Component
public class EndPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndPoint.class);

    private final JsonParser jsonParser = new JsonParser();

    private final Gson gson = new Gson();

    private final String channel  = "ok_sub_spot_btc_depth";

    @Autowired
    private Disruptor<MarketEvent> disruptor;

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
        LOGGER.info("received message:"+message);
        Model model = gson.fromJson(message,Model[].class)[0];
        if (StringUtils.equals(model.getChannel(),"addChannel")) {
            Map<String,String> map = (Map<String, String>) model.getData();
            if(map.get("result").equals("true")){

            }
        }else if(model.getChannel().equals(channel)){
            Map<String,List<List<String>>> map = (Map<String, List<List<String>>>) model.getData();
            List<List<String>> asks = map.get("asks");
            String price = asks.get(0).get(0);
            String volume = asks.get(0).get(1);
//            disruptor.publishEvent((event,sequence,arg)->{event},asks.get(0));
        }
    }

    @OnError
    public void processError(Throwable t) {
        LOGGER.error("got error from server", t);
    }
}
