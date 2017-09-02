package chaz.trade;

import chaz.trade.core.MarketEvent;
import chaz.trade.core.TradeHandler;
import chaz.trade.input.huobi.HuobiWSConnector;
import chaz.trade.input.okcoin.OkcoinWSConnector;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.Executors;

/**
 * Created by chengzhang.wang on 2017/8/19.
 */
@Component
@SpringBootApplication
public class Application {
    private static final Disruptor<MarketEvent> disruptor = new Disruptor<>(() -> new MarketEvent(), 1024 * 1024, Executors.defaultThreadFactory(), ProducerType.MULTI, new YieldingWaitStrategy());
    @Autowired
    private TradeHandler tradeHandler;
    @Autowired
    private HuobiWSConnector huobiWSConnector;

    @Autowired
    private OkcoinWSConnector okcoinWSConnector;

    public static Disruptor<MarketEvent> getDisuptor() {
        return disruptor;
    }

    @PostConstruct
    private void start() {
        disruptor.handleEventsWith(tradeHandler);
        disruptor.start();
        huobiWSConnector.start();
        okcoinWSConnector.start();
    }

    @PreDestroy
    private void stop() {
        disruptor.shutdown();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
