package chaz.trade;

import chaz.trade.model.MarketData;
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
    private Disruptor<MarketData> disruptor;

    @PostConstruct
    private void start() {
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
