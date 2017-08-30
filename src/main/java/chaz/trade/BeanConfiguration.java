package chaz.trade;

import chaz.trade.core.MarketEvent;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/28.
 */
@Configuration
public class BeanConfiguration {
    @Bean
    public Disruptor<MarketEvent> disruptor() {
        return new Disruptor<>(() -> new MarketEvent(), 1024 * 1024, Executors.defaultThreadFactory(), ProducerType.MULTI, new YieldingWaitStrategy());
    }
}
