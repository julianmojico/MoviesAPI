package configurations;


import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class MoviesAPIConfiguration extends Configuration {

    @NotEmpty
    private String appName = "defaultAppName";

    @JsonProperty
    public String getAppName() {
        return appName;
    }

    @JsonProperty
    public void setAppName(String appName) {
        this.appName = appName;
    }
}