package models;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum FilterType {

    @JsonProperty("movies") MOVIES,
    @JsonProperty("series") SERIES,
    @JsonProperty("episodes") EPISODES,
    @JsonProperty("game") GAME;

}
