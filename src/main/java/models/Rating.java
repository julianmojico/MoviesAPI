package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rating {

    private String source;
    private String value;

    public Rating() {

    }

    @JsonProperty("Source")
    public String getSource() {
        return source;
    }
    @JsonProperty("Value")
    public String getValue() {
        return value;
    }
}
