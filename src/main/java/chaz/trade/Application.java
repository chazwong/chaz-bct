package chaz.trade;

import chaz.trade.core.MarketEvent;
import chaz.trade.core.TradeHandler;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by chengzhang.wang on 2017/8/19.
 */
@Component
@SpringBootApplication
public class Application {
    @Autowired
    private Disruptor<MarketEvent> disruptor;
    @Autowired
    private TradeHandler tradeHandler;

    @PostConstruct
    private void start() {
        disruptor.handleEventsWith(tradeHandler);
        disruptor.start();
    }

    @PreDestroy
    private void stop() {
        disruptor.shutdown();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
