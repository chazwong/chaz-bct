package chaz.trade.login;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Created by chengzhang.wang on 2017/8/19.
 */
public class LoginService {
    private Client client;
    public void doLogin(){
        WebResource webResource = client
                .resource("http://localhost:8080/RESTfulExample/rest/json/metallica/post");

        String input = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";

        ClientResponse response = webResource.type("application/json")
                .post(ClientResponse.class, input);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
    }
}
