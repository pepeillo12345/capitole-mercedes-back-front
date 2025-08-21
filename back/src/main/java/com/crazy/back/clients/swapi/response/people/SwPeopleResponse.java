package com.crazy.back.clients.swapi.response.people;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents a response from the Star Wars API for a person.
 * This class maps the JSON response fields to Java fields.
 */
@Data
public class SwPeopleResponse {

    @JsonProperty("birth_year")
    private String birthYear;

    @JsonProperty("eye_color")
    private String eyeColor;

    private List<String> films;

    private String gender;

    @JsonProperty("hair_color")
    private String hairColor;

    private String height;

    private String homeworld;

    private String mass;

    private String name;

    @JsonProperty("skin_color")
    private String skinColor;

    private OffsetDateTime created;

    private OffsetDateTime edited;

    private List<String> species;

    private List<String> starships;

    private String url;

    private List<String> vehicles;
}
