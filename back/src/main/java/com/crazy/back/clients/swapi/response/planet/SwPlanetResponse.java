package com.crazy.back.clients.swapi.response.planet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class SwPlanetResponse {

    private String climate;

    private OffsetDateTime created;

    private String diameter;

    private OffsetDateTime edited;

    private List<String> films;

    private String gravity;

    private String name;

    @JsonProperty("orbital_period")
    private String orbitalPeriod;

    private String population;

    private List<String> residents;

    @JsonProperty("rotation_period")
    private String rotationPeriod;

    @JsonProperty("surface_water")
    private String surfaceWater;

    private String terrain;

    private String url;
}
