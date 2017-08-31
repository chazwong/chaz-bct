package chaz.trade.input.okcoin;

/**
 * Created by Administrator on 2017/8/31.
 */
public class SubscribeRequestModel {
    private String event;
    private String channel;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
