package chaz.trade;

/**
 * Created by chengzhang.wang on 2017/8/22.
 */
public class MarketData {
    private float price;
    private float volume;
    private int level;
    private byte type;//0-bid,1-ask

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
