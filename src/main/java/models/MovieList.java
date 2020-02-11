package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieList {
    private List<Movie> movieList;
    
    public MovieList(List<Movie> movieList){
        this.setMovieList(movieList);
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @Override
    public boolean equals(Object o){
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof MovieList)) {
            return false;
        }

        MovieList m = (MovieList) o;

        return o.equals(this);
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + this.movieList.hashCode();
        return result;
    }
}
