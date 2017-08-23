package chaz.trade;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by chengzhang.wang on 2017/8/22.
 */
public class OrderBook {
    ConcurrentLinkedQueue<BTCOrder> bidBook;
    ConcurrentLinkedQueue<BTCOrder> askBook;
}
