package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.ws.rs.core.Response;

public class APIError {

    @JsonProperty
    private Response.Status status;
    @JsonProperty
    private String message;
    @JsonProperty
    private String errors;

    public APIError(Response.Status status, String message, String errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public APIError(){};
}
