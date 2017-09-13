package chaz.trade;

import chaz.trade.connector.okcoin.OkcoinWSConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

/**
 * Created by Administrator on 2017/9/13.
 */
@SpringBootApplication
public class SingleMarketHedgeApplication {
    @Autowired
    private OkcoinWSConnector okcoinWSConnector;

    public void start() {
        okcoinWSConnector.start();
    }

    @PreDestroy
    private void stop(){
        okcoinWSConnector.stop();
    }

    public static void main(String[] args) throws InterruptedException {
        SingleMarketHedgeApplication app = SpringApplication.run(SingleMarketHedgeApplication.class, args).getBean(SingleMarketHedgeApplication.class);
        app.start();
        synchronized (app){
            app.wait();
        }
    }
}
