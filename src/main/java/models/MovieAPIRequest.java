package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(alphabetic=true)
public class MovieAPIRequest {

    @NotEmpty
    private String id;
    private String movieTitle;
    private FilterType filterType;
    private String year;
    private int page = 1;
    private boolean isValidRequest = false;

    public MovieAPIRequest() {

    }

    public MovieAPIRequest(String id) {
        this.id = id;
        isValidRequest = true;
    }

    public String getId() {
        return id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public String getYear() {
        return year;
    }

    public int getPage() {
        return page;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id,movieTitle,filterType,year,page);
    }

    public boolean isNullRequest() {
        return ((id == null)
                && (movieTitle == null)
                && (movieTitle == null)
                && (filterType == null)
                && (year == null));
    }


}
