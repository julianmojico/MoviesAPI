package resources;

import ch.qos.logback.core.status.Status;
import models.APIError;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.util.Optional;

public class APIResponseUtils {

    public static ResponseBuilder badRequest() {
        APIError apiError = new APIError(Response.Status.BAD_REQUEST, "Malformed request", "Request syntax is wrong");
        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST)
                .entity(apiError)
                .type(MediaType.APPLICATION_JSON);
        return builder;
    }

    public static ResponseBuilder badRequest(String message) {
        APIError apiError = new APIError(Response.Status.BAD_REQUEST, "Malformed request", message);
        Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST)
                .entity(apiError)
                .type(MediaType.APPLICATION_JSON);
        return builder;
    }

    public static ResponseBuilder serverError(String errorMessage) {
        APIError apiError = new APIError(Response.Status.INTERNAL_SERVER_ERROR, "Internal Server Error", errorMessage);
        Response.ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(apiError)
                .type(MediaType.APPLICATION_JSON);
        return builder;
    }

    public static ResponseBuilder otherStatus(String message, Response.Status status) {
        APIError apiError = new APIError(status, message, "");
        Response.ResponseBuilder builder = Response.status(status)
                .entity(apiError)
                .type(MediaType.APPLICATION_JSON);
        return builder;
    }

    public static ResponseBuilder okWithContent(Object content){
        //Content must be serializable
        Response.ResponseBuilder builder = Response.status(Response.Status.OK)
                .entity(content)
                .type(MediaType.APPLICATION_JSON);
        return builder;
    }

    public static ResponseBuilder notFound(){
        APIError apiError = new APIError(Response.Status.NOT_FOUND, "Resource Not Found", "");
        Response.ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND)
                .entity(apiError)
                .type(MediaType.APPLICATION_JSON);
        return builder;
    }

    public static ResponseBuilder methodNotAllowed(){
        //Content must be serializable
        APIError apiError = new APIError(Response.Status.METHOD_NOT_ALLOWED, "Method not allowed", "HTTP method in this request is not allowed by the server");
        Response.ResponseBuilder builder = Response.status(Response.Status.METHOD_NOT_ALLOWED)
                .entity(apiError)
                .type(MediaType.APPLICATION_JSON);
        return builder;
    }

    public static Response serverError(Exception e) {
        String errorMessage;
        if (e.getMessage() != null) {
            errorMessage = e.getMessage();
        } else {
            errorMessage = "There was an error in the server while processing the request";
        }
        return APIResponseUtils.serverError(errorMessage).build();
    }
}
