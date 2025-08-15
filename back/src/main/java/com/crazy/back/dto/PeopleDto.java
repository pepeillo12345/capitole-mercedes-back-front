package com.crazy.back.dto;

import com.crazy.back.util.sorting.SortType;
import com.crazy.back.util.sorting.Sortable;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class PeopleDto {
    private String birthYear;
    private String eyeColor;
    private List<String> films;
    private String gender;
    private String hairColor;
    private String height;
    private String homeworld;
    private String mass;
    @Sortable(type = SortType.STRING)
    private String name;
    private String skinColor;
    @Sortable(type = SortType.DATE)
    private OffsetDateTime created;
    private OffsetDateTime edited;
    private List<String> species;
    private List<String> starships;
    private String url;
    private List<String> vehicles;
}
