import configurations.MoviesAPIConfiguration;
import health.TemplateHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resources.MovieController;
import service.OMDBMovieService;

import javax.ws.rs.client.Client;

public class APIApplication extends Application<MoviesAPIConfiguration> {

    private final String apiKey = System.getProperty("apiKey");
    private final String baseUrl = System.getProperty("baseUrl");

    public static void main(String[] args) throws Exception {
        new APIApplication().run(args);
    }

    @Override
    public String getName() {
        return "MoviesAPI";
    }

    @Override
    public void initialize(Bootstrap<MoviesAPIConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(MoviesAPIConfiguration moviesAPIConfiguration, Environment environment) {

        if (!(baseUrl == null || apiKey == null)) {
            //JerseyClient initialization
            final Client client = new JerseyClientBuilder(environment).using(new JerseyClientConfiguration())
                    .build(getName());

            //Service instantiation
            OMDBMovieService movieService = new OMDBMovieService(apiKey,baseUrl);

            //Service injection
            environment.jersey().register(new MovieController(movieService));

            final TemplateHealthCheck healthCheck =
                    new TemplateHealthCheck(moviesAPIConfiguration.getAppName());
            environment.healthChecks().register("appHealthcheck", healthCheck);
        } else {
            throw new RuntimeException("baseApiUrl and apiKey are required parameters needed by the JVM");
        }

    }
}