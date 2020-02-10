package resources;

import com.codahale.metrics.annotation.Timed;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import models.MovieAPIRequest;
import models.MovieDetails;
import service.GenericCacheService;
import service.OMDBMovieService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class MovieController {

    private OMDBMovieService movieService;
    private GenericCacheService moviesCache;
    private GenericCacheService movieDetailsCache;

    public MovieController(OMDBMovieService movieService, GenericCacheService moviesCache, GenericCacheService movieDetailsCache) {
        this.movieService = movieService;
        this.moviesCache = moviesCache;
        this.movieDetailsCache = movieDetailsCache;
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
            MovieDetails cachedObject = (MovieDetails) this.movieDetailsCache.find(request);
            if (cachedObject != null) {
                return APIResponseUtils.okWithContent(cachedObject).build();
            } else {
                Response response = callMovieService(request);
                response.bufferEntity();
                MovieDetails details = (MovieDetails) response.getEntity();
                this.movieDetailsCache.save(request, details);
                return response;
            }
        }
    }

    @Path("/movies")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response getMovies(MovieAPIRequest request) {
        return callMovieService(request);
    }

    private Response callMovieService(MovieAPIRequest request) {
        Response.ResponseBuilder response;
        try {
            response = movieService.fetchMovies(request);
            return response.build();
        } catch (Exception e) {
            String errorMessage;
            if (e.getMessage() != null) {
                errorMessage = e.getMessage();
            } else {
                errorMessage = "There was an error in the server while processing the request";
            }
            return APIResponseUtils.serverError(errorMessage).build();
        }
    }
}
