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
    private FilterType type;
    private String year;
    private int page = 1;
    private boolean includeDetails = false;

    public MovieAPIRequest() {
    }

    public MovieAPIRequest(String id) {
        this.id = id;
        this.includeDetails = true;
    }

    public String getId() {
        return id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public FilterType getType() {
        return type;
    }

    public String getYear() {
        return year;
    }

    public int getPage() {
        return page;
    }

    public boolean isNullRequest() {
        return ((id == null)
                && (movieTitle == null)
                && (movieTitle == null)
                && (type == null)
                && (year == null));
    }

    public boolean includeDetails() {
        return includeDetails;
    }

    @Override
    public boolean equals(Object o){
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof MovieAPIRequest)) {
            return false;
        }

        MovieAPIRequest m = (MovieAPIRequest) o;

        return Objects.equals(this.id,m.id)
                && Objects.equals(this.movieTitle,m.movieTitle)
                && this.includeDetails == m.includeDetails
                && this.page == m.page
                && Objects.equals(this.type,m.type);
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + (id!=null?id.hashCode():0);
        result = 31 * result + (movieTitle!=null?movieTitle.hashCode():0);
        result = 31 * result + (includeDetails==true?1:0);
        result = 31 * result + page;
        result = 31 * result + (type !=null? type.hashCode():0);
        return result;
    }
}
