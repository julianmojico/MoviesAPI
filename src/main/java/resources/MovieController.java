package resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.caching.CacheControl;
import models.MovieAPIRequest;
import service.GenericCacheService;
import service.OMDBMovieService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class MovieController {

    private OMDBMovieService movieService;
    private GenericCacheService moviesCache;
    private GenericCacheService detailsCache;

    public MovieController(OMDBMovieService movieService, GenericCacheService moviesCache, GenericCacheService detailsCache) {
        this.movieService = movieService;
        this.moviesCache = moviesCache;
        this.detailsCache = detailsCache;
    }

    @CacheControl(maxAge = 30, maxAgeUnit = TimeUnit.DAYS)
    @Path("/movie/{id}")
    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieById(@PathParam("id") String id) {

        MovieAPIRequest request = new MovieAPIRequest(id);
        if (id == null || id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "At least one of the parameters id,title,year must exist").build();
        } else {
            return safeServiceRequest(request, detailsCache);
        }
    }

    @CacheControl(maxAge = 30, maxAgeUnit = TimeUnit.DAYS)
    @Path("/movies")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public Response getMovies(MovieAPIRequest request) {

        try {
            if (!(request == null)) {
                return safeServiceRequest(request, moviesCache);
            } else {
                return Response.ok(null, MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception e) {
            throw new WebApplicationException(404);
        }
    }

    private Response safeServiceRequest(MovieAPIRequest request, GenericCacheService cache) {
        Response.ResponseBuilder response;
        try {
            Object cachedObject = cache.find(request);
            if (cachedObject != null) {
                return APIResponseUtils.okWithContent(cachedObject).build();
            } else {
                response = movieService.fetchMovies(request);
                cache.save(request, (response.build().getEntity()));
                return response.build();
            }
        } catch (Exception e) {
            return APIResponseUtils.serverError(e);
        }
    }
}
