package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Movie;
import models.MovieAPIRequest;
import org.ehcache.core.Ehcache;
import resources.APIResponseUtils;

import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MovieService {

    private String apiKey;
    private String baseUrl;
    private Client client;
    private Ehcache<MovieAPIRequest, Movie> cache;


    public MovieService(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.client = ClientBuilder.newClient();
        //todo: add cache service attribute
    }

    public Response.ResponseBuilder fetchMovies(MovieAPIRequest request) throws Exception {

        boolean retrieveList = true;
        Response.ResponseBuilder responseBuilder = null;

        if (!request.isNullRequest()) {

            //Perform public API call
            Response response = callPublicAPI(request, retrieveList);
            List<Movie> a = new ArrayList<>();
            responseBuilder = buildResponse(response, a);

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

    private <T> Response.ResponseBuilder buildResponse(Response response, T type) throws JsonProcessingException {
        Response.ResponseBuilder responseBuilder = null;

        if (response.hasEntity() && response.getStatus() == Response.Status.OK.getStatusCode()) {
            response.bufferEntity();
            JsonNode json = response.readEntity(ObjectNode.class).remove("Search");
            if (json.has("Error")) {
                //Wrap error message given by public API in our response
                return APIResponseUtils.serverError(json.get("Error").asText());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                List<Movie> movies = mapper.readValue(json.toString(), new TypeReference<List<Movie>>() {
                });
                responseBuilder = APIResponseUtils.okWithContent(movies);
            }
        } else {
            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                responseBuilder = APIResponseUtils.okWithContent("{}");
            } else {
                responseBuilder = APIResponseUtils.otherStatus("Unknown server error", Response.Status.fromStatusCode(response.getStatus()));
            }
        }
        return responseBuilder;
    }

        private URI buildQueryFilters (MovieAPIRequest movieAPIRequest,boolean isList){

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
