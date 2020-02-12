package configurations;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;

public class MoviesAPIConfiguration extends Configuration {

    @NotEmpty
    private String appName = "defaultAppName";

    @NotEmpty
    private String apiKey;

    @NotEmpty
    private String baseUrl;

    @NotEmpty
    private String environment;

    private LinkedHashMap<String, String> server;

    @JsonProperty
    public String getAppName() {
        return appName;
    }

    @JsonProperty
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @JsonProperty
    public String getApiKey() {
        return apiKey;
    }

    @JsonProperty
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @JsonProperty
    public String getBaseUrl() {
        return baseUrl;
    }

    @JsonProperty
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @JsonProperty
    public String getEnvironment() {
        return environment;
    }

    @JsonProperty
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    @JsonProperty
    public LinkedHashMap getServer() {
        return server;
    }

    @JsonProperty
    public void setServer(LinkedHashMap server) {
        this.server = server;
    }
}