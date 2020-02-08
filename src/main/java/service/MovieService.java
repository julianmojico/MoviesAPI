package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Movie;
import models.MovieAPIRequest;
import org.ehcache.core.Ehcache;
import resources.APIResponseUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;
import java.util.Optional;


public class MovieService {

    private final String apiKey = System.getProperty("apiKey");
    private final String baseUrl = System.getProperty("baseUrl");
    private Client client;
    private Ehcache<MovieAPIRequest, Movie> cache;


    public MovieService(Client client) {
        this.client = ClientBuilder.newClient();
        //todo: add cache service attribute
    }

    public Optional<Movie> fetchMovie(MovieAPIRequest request) {

        boolean retrieveList = false;
        Optional<Movie> output;

        URI uri = buildQueryFilters(request, retrieveList);
        WebTarget webTarget = client.target(uri);
        Response response = null;

        try {
            response = webTarget.request(MediaType.APPLICATION_JSON).get();
        } catch (Exception e) {
            throw e;
        }
        if (response != null && response.getStatus() == Response.Status.OK.getStatusCode() && response.hasEntity()) {

            Movie movie = response.readEntity(Movie.class);
            output = Optional.of(movie);

        } else {
            output = Optional.empty();
        }

        return output;

    }

    private static <T> T fromJSON(final TypeReference<T> type, final String json) {
        T data = null;
        try {
            data = new ObjectMapper().readValue(json, type);
        } catch (Exception e) {
            // Handle the problem
        }
        return data;
    }

    public Response.ResponseBuilder fetchMovies(MovieAPIRequest request) {

        boolean retrieveList = true;
        Response.ResponseBuilder responseBuilder = null;

        if (!request.isNullRequest()) {

            //Perform public API call
            Response response = callPublicAPI(request, retrieveList);
            responseBuilder = buildResponse(responseBuilder, response);

        } else {
            responseBuilder = APIResponseUtils.badRequest();
        }
        return responseBuilder;

    }

    private Response callPublicAPI(MovieAPIRequest request, boolean retrieveList) {
        URI uri = buildQueryFilters(request, retrieveList);
        WebTarget webTarget = client.target(uri);
        return webTarget.request(MediaType.APPLICATION_JSON).get();
    }

    private Response.ResponseBuilder buildResponse(Response.ResponseBuilder responseBuilder, Response response) {
        if (response.hasEntity()) {
            JsonNode json = response.readEntity(JsonNode.class);
            if (json.has("Error")) {
                //Wrap error message given by public API in our response
                responseBuilder = APIResponseUtils.serverError(json.get("Error").asText());
            } else {
                String jsonString = json.toString();
                List<Movie> movies = fromJSON(new TypeReference<List<Movie>>() {
                }, jsonString);
                responseBuilder = APIResponseUtils.okWithContent(movies);
            }
        }
        if (!response.hasEntity() && (response.getStatus() == Response.Status.OK.getStatusCode())) {
            responseBuilder = APIResponseUtils.okWithContent("{}");
        }
        return responseBuilder;
    }

    private URI buildQueryFilters(MovieAPIRequest movieAPIRequest, boolean isList) {

        UriBuilder uriBuilder = UriBuilder.fromUri(baseUrl);
        uriBuilder.queryParam("apikey", apiKey);

        if (movieAPIRequest.getMovieTitle() != null) {
            if (isList) {
                uriBuilder.queryParam("s", movieAPIRequest.getMovieTitle());
            } else {
                uriBuilder.queryParam("t", movieAPIRequest.getMovieTitle());
            }
        }
        if (movieAPIRequest.getId() != null) {
            uriBuilder.queryParam("i", movieAPIRequest.getId());
        }
        if (movieAPIRequest.getYear() != null) {
            uriBuilder.queryParam("y", movieAPIRequest.getYear());
        }
        if (movieAPIRequest.getPage() != 0) {
            uriBuilder.queryParam("page", movieAPIRequest.getPage());
        }
        if (movieAPIRequest.getFilterType() != null) {
            uriBuilder.queryParam("filterType", movieAPIRequest.getFilterType());
        }
        return uriBuilder.build(movieAPIRequest.getMovieTitle(), movieAPIRequest.getPage());
    }
}
