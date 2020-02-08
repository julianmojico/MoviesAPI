package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class APIError {

    @JsonProperty
    private Response.Status status;
    @JsonProperty
    private String message;
    @JsonProperty
    private List<String> errors;

    public APIError(Response.Status status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public APIError(){};

    public APIError(Response.Status status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }
}
