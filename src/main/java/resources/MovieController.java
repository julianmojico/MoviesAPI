package resources;

import com.codahale.metrics.annotation.Timed;
import models.MovieAPIRequest;
import models.MovieDetails;
import models.MovieList;
import service.GenericCacheService;
import service.OMDBMovieService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class MovieController {

    private OMDBMovieService movieService;
    private GenericCacheService<MovieAPIRequest,MovieList> moviesCache;
    private GenericCacheService<MovieAPIRequest,MovieDetails> detailsCache;

    public MovieController(OMDBMovieService movieService, GenericCacheService moviesCache, GenericCacheService detailsCache) {
        this.movieService = movieService;
        this.moviesCache = moviesCache;
        this.detailsCache = detailsCache;
    }

    @Path("/movie/{id}")
    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieById(@PathParam("id") String id) {

        MovieAPIRequest request = new MovieAPIRequest(id);
        if (id == null || id.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "At least one of the parameters id,title,year must exist").build();
        } else {
            return safeServiceRequest(request,detailsCache);
        }
    }

    @Path("/movies")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response getMovies(MovieAPIRequest request) {
        return safeServiceRequest(request, moviesCache);
    }


    private Response safeServiceRequest(MovieAPIRequest request, GenericCacheService cache) {
        Response.ResponseBuilder response;
        try {
            Object cachedObject = cache.find(request);
            if (cachedObject != null) {
                return APIResponseUtils.okWithContent(cachedObject).build();
            } else {
                response = movieService.fetchMovies(request);
                return response.build();
            }
        } catch (Exception e) {
            return APIResponseUtils.serverError(e);
        }
    }
}
