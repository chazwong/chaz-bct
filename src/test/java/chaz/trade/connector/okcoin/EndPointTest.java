package chaz.trade.connector.okcoin;

import org.junit.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/9/13.
 */
public class EndPointTest {

    @Test
    public void testFee(){
        assert 0f==0.0;
        assert 0.0f==0;
    }

    @Test
    public void testSort(){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("api_key", "apiKey");
        parameters.put("symbol", "etc");
        parameters.put("type", "buy");
        parameters.put("price", String.valueOf(0.1));
        parameters.put("amount", String.valueOf(0.2));
        parameters.put("secret_key", "secretKey");
        System.out.println(parameters.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue().toString())).collect(Collectors.joining("&")));
    }
}