package chaz.trade;

import chaz.trade.model.MarketData;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Administrator on 2017/8/28.
 */
@Configuration
public class BeanConfiguration {
    @Bean
    public Disruptor<MarketData> disruptor() {
        return new Disruptor<MarketData>(() -> new MarketData(), 1024 * 1024, DaemonThreadFactory.INSTANCE, ProducerType.MULTI, new YieldingWaitStrategy());
    }
}
