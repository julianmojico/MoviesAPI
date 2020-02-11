import configurations.MoviesAPIConfiguration;
import health.TemplateHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import models.MovieAPIRequest;
import models.MovieDetails;
import models.MovieList;
import org.ehcache.Cache;
import resources.MovieController;
import service.EhCacheInitializer;
import service.GenericCacheService;
import service.MoviesCacheService;
import service.OMDBMovieService;

import javax.ws.rs.client.Client;
import java.util.ArrayList;
import java.util.List;

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

            //OMDB public API service instantiation
            OMDBMovieService movieService = new OMDBMovieService(apiKey,baseUrl);

            //Cache service instantiation
            EhCacheInitializer cacheInitializer = new EhCacheInitializer();

            //MovieList cache instantiation
            Cache<MovieAPIRequest,MovieList> movieListCache = cacheInitializer.createCache("movieListCache", MovieAPIRequest.class, MovieList.class,1000);
            MoviesCacheService moviesCacheService = new MoviesCacheService(movieListCache);

            //MovieDetails cache instantiation
            Cache<MovieAPIRequest,MovieDetails> movieDetailsCache = cacheInitializer.createCache("movieDetailsCache", MovieAPIRequest.class, MovieDetails.class,1000);
            MoviesCacheService movieDetailsCacheService = new MoviesCacheService(movieDetailsCache);

            //Services injection
            environment.jersey().register(new MovieController(movieService,moviesCacheService,movieDetailsCacheService));

            final TemplateHealthCheck healthCheck =
                    new TemplateHealthCheck(moviesAPIConfiguration.getAppName());
            environment.healthChecks().register("appHealthcheck", healthCheck);
        } else {
            throw new RuntimeException("baseApiUrl and apiKey are required parameters needed by the JVM");
        }

    }
}