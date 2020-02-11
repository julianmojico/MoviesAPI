package health;

import com.codahale.metrics.health.HealthCheck;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class APIHealthCheck extends HealthCheck {

    private Client client;
    private String appName;

    public APIHealthCheck(String appName) {
        this.appName = appName;
        this.client = ClientBuilder.newClient();
    }

    @Override
    protected Result check() throws Exception {
        String uri = "http://127.0.0.1:8080/api/movies";
        WebTarget webTarget = client.target(uri);
        Response response = webTarget.request(MediaType.APPLICATION_JSON).get();

        // If response is a 20x response code pass it.
        if (response.getStatus() == 200) {
            return Result.healthy("Application health looks good!");
        }
        return Result.unhealthy("code: %s - body: %s", response.getStatus(), response.getEntity().toString());
    }
}
