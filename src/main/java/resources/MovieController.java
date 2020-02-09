package resources;

import com.codahale.metrics.annotation.Timed;
import models.MovieAPIRequest;
import service.MovieService;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
    public Response getMovieById(@PathParam("id") String id) {

        MovieAPIRequest request = new MovieAPIRequest(id);
        Response.ResponseBuilder response;
        if (id == null || id.isEmpty() || request.isNullRequest()) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "At least one of the parameters id,title,year must exist").build();
        } else {
            return movieServiceRequest(request);
        }
    }

    private Response movieServiceRequest(MovieAPIRequest request) {
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

    @Path("/movie")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response getMovie(MovieAPIRequest request) {

        Response.ResponseBuilder response;

        //TODO: if endpoint returns list of movies, send first match.
        return movieServiceRequest(request);
    }

    @Path("/movies")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Timed
    public Response getMovies(MovieAPIRequest request) {

        Response.ResponseBuilder response;

        if (request.getId() != null && !request.getId().isEmpty()) {
            return APIResponseUtils.badRequest("id parameter must not be sent since this endpoint returns a list of movies").build();
        }
        return movieServiceRequest(request);
    }
}
