import exceptions.EntityNotFoundMapper;
import configurations.MoviesAPIConfiguration;
import exceptions.GeneralErrorMapper;
import exceptions.MethodNotAllowedMapper;
import health.APIHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import models.MovieAPIRequest;
import models.MovieDetails;
import org.ehcache.Cache;
import resources.MovieController;
import service.EhCacheManager;
import service.MoviesCacheService;
import service.OMDBMovieService;

import java.time.Duration;
import java.util.*;

public class APIApplication extends Application<MoviesAPIConfiguration> {

    public static void main(String[] args) throws Exception {
        new APIApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<MoviesAPIConfiguration> bootstrap) {
    }

    @Override
    public void run(MoviesAPIConfiguration moviesAPIConfiguration, Environment environment) {

        String baseUrl = moviesAPIConfiguration.getBaseUrl();
        String apiKey = moviesAPIConfiguration.getApiKey();

        //OMDB public API service instantiation
        OMDBMovieService movieService = new OMDBMovieService(apiKey, baseUrl);

        //Cache service instantiation
        EhCacheManager cacheInitializer = new EhCacheManager();

        //Let dropwizard handle cache´s lifecycle
        environment.lifecycle().manage(cacheInitializer);

        //MovieList cache instantiation
        Cache<MovieAPIRequest, ArrayList> movieListCache = cacheInitializer.createCache("movieListCache", MovieAPIRequest.class, ArrayList.class, 5000, Duration.ofDays(30L));
        MoviesCacheService moviesCacheService = new MoviesCacheService(movieListCache);

        //MovieDetails cache instantiation
        Cache<MovieAPIRequest, MovieDetails> movieDetailsCache = cacheInitializer.createCache("movieDetailsCache", MovieAPIRequest.class, MovieDetails.class, 5000, Duration.ofDays(30L));
        MoviesCacheService movieDetailsCacheService = new MoviesCacheService(movieDetailsCache);

        //Services injection
        environment.jersey().register(new MovieController(movieService, moviesCacheService, movieDetailsCacheService));

        //Customized exception mappers
        environment.jersey().register(EntityNotFoundMapper.class);
        environment.jersey().register(MethodNotAllowedMapper.class);
        environment.jersey().register(GeneralErrorMapper.class);

        //Healthcheck
        String appName = moviesAPIConfiguration.getAppName();
        final APIHealthCheck healthCheck = new APIHealthCheck(appName);
        environment.healthChecks().register(appName, healthCheck);

    }
}