package chaz.trade.input;

import chaz.trade.input.Huibi.SubRequestModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * Created by Administrator on 2017/8/28.
 */
@Component
public class HuobiInputer extends StompSessionHandlerAdapter {
    private final String url = "wss://api.huobi.pro/ws";

    public void start() {
        WebSocketClient client = new StandardWebSocketClient();

        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.connect(url, this);
    }

    public void stop() {

    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        Gson gson = new GsonBuilder().create();
        SubRequestModel subRequestModel = new SubRequestModel();
        subRequestModel.setId("id");
        subRequestModel.setSub("market.btccny.depth.step1");
        session.send("destination",gson.toJson(subRequestModel));
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {

    }
}
