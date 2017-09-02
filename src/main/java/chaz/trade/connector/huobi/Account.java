package chaz.trade.connector.huobi;

import chaz.trade.model.Balance;

/**
 * Created by Administrator on 2017/9/2.
 */
public class Account extends Balance{
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
