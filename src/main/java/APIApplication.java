import configurations.MoviesAPIConfiguration;
import health.TemplateHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import resources.MovieController;
import service.MovieService;

import javax.ws.rs.client.Client;

public class APIApplication extends Application<MoviesAPIConfiguration> {
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
        //JerseyClient initialization
        final Client client = new JerseyClientBuilder(environment).using(new JerseyClientConfiguration())
                .build(getName());

        //Service instantiation
        MovieService movieService = new MovieService(client);

        //Service injection
        environment.jersey().register(new MovieController(movieService));

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(moviesAPIConfiguration.getAppName());
        environment.healthChecks().register("appHealthcheck", healthCheck);
    }

}