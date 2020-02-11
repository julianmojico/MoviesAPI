package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.MovieAPIRequest;
import models.MovieDetails;
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
public class OMDBMovieService {

    private String apiKey;
    private String baseUrl;
    private Client client;


    public OMDBMovieService(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.client = ClientBuilder.newClient();
        //todo: add cache service attribute
    }

    public Response.ResponseBuilder fetchMovies(MovieAPIRequest request) throws Exception {

        Response.ResponseBuilder responseBuilder;

        if (!request.isNullRequest()) {
            //Perform public API call
            Response response = callPublicAPI(request);
            responseBuilder = buildResponse(response, request.includeDetails());

        } else {
            responseBuilder = APIResponseUtils.okWithContent("{}");
        }
        return responseBuilder;

    }

    private Response callPublicAPI(MovieAPIRequest request) {
        URI uri = buildQueryFilters(request);
        WebTarget webTarget = client.target(uri);
        return webTarget.request(MediaType.APPLICATION_JSON).get();
    }

    private Response.ResponseBuilder buildResponse(Response response, boolean isDetailed) throws JsonProcessingException {
        Response.ResponseBuilder responseBuilder;
        response.bufferEntity();
        if (response.hasEntity() && response.getStatus() == Response.Status.OK.getStatusCode()) {
            JsonNode json = response.readEntity(JsonNode.class);
            if (json.has("Error")) {
                //Wrap error message given by public API in our response
                return APIResponseUtils.serverError(json.get("Error").asText());
            } else {
                Object body;
                if (isDetailed) {
                    body = mapMovieDetail(json);
                } else {
                    body = mapMovieList(json);
                }
                responseBuilder = APIResponseUtils.okWithContent(body);
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

    private static MovieDetails mapMovieDetail(JsonNode json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json.toString(), new TypeReference<MovieDetails>() {
        });
    }

    private static List mapMovieList(JsonNode json) throws JsonProcessingException {
        json = json.findValue("Search");
        //TODO: if json is not list [] then map a single Movie and add the [] to the response
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json.toString(), ArrayList.class);
    }

    private URI buildQueryFilters(MovieAPIRequest movieAPIRequest) {

        UriBuilder uriBuilder = UriBuilder.fromUri(baseUrl);
        uriBuilder.queryParam("apikey", apiKey);

        if (movieAPIRequest.includeDetails()){
            //if details were requested then just ID is needed since it´´ not considered a search request
            uriBuilder.queryParam("i", movieAPIRequest.getId());
        } else {

            //search request parameters mapping to query params
            if (movieAPIRequest.getMovieTitle() != null) {
                uriBuilder.queryParam("s", movieAPIRequest.getMovieTitle());
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
        }
        return uriBuilder.build(movieAPIRequest.getMovieTitle(), movieAPIRequest.getPage());
    }
}
