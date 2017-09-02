package chaz.trade.model;

/**
 * Created by Administrator on 2017/9/2.
 */
public class Balance {
    private double available;
    private double frozen;

    public double getAvailable() {
        return available;
    }

    public void setAvailable(double available) {
        this.available = available;
    }

    public double getFrozen() {
        return frozen;
    }

    public void setFrozen(double frozen) {
        this.frozen = frozen;
    }
}
