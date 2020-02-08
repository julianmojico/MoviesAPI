package resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.caching.CacheControl;
import models.Movie;
import models.MovieAPIRequest;
import service.MovieService;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
public class MovieController {

    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Path("/movie/{id}")
    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @CacheControl(maxAge = 6, maxAgeUnit = TimeUnit.HOURS)
    public Response getMovieById(@PathParam("id") String id) {

        MovieAPIRequest request = new MovieAPIRequest(id);
        if (id == null || !request.isNullRequest()) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "At least one of the parameters id,title,year must exist").build();
        } else {
            Optional<Movie> optional;
            try {
                optional = movieService.fetchMovie(request);
                if (optional.isPresent()) {
                    return Response.ok(optional.get()).build();
                } else return Response.noContent().build();
            } catch (WebApplicationException e) {
                return Response.serverError().build();
            } catch (Exception e) {
                return Response.serverError().build();
            }
        }
    }

    @Path("/movies")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response getMovies(MovieAPIRequest request) {

        //404,500,405
        if (request == null || request.isNullRequest()) {
            return  APIResponseUtils.badRequest().build();
        } else {
            Optional<List<Movie>> optional;
            try {
                optional = movieService.fetchMovies(request);
                if (optional.isPresent()) {
                    return APIResponseUtils.okWithContent(optional.get()).build();
                } else return APIResponseUtils.notFound().build();
            } catch (Exception e) {
                return APIResponseUtils.serverError(e).build();
            }
        }
    }

}
