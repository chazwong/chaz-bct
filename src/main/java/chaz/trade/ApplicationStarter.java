package chaz.trade;

import chaz.trade.model.MarketData;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * Created by chengzhang.wang on 2017/8/19.
 */
public class ApplicationStarter {
    private Disruptor<MarketData> disruptor;
    private void start(){
        disruptor.start();
    }
    private void stop(){
        disruptor.shutdown();
    }
    public static void main(String [] args){

    }
}
