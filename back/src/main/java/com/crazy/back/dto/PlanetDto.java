package com.crazy.back.dto;

import com.crazy.back.util.sorting.SortType;
import com.crazy.back.util.sorting.Sortable;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class PlanetDto {
    private String climate;
    @Sortable(type = SortType.DATE)
    private OffsetDateTime created;
    private String diameter;
    private OffsetDateTime edited;
    private List<String> films;
    private String gravity;
    @Sortable(type = SortType.STRING)
    private String name;
    private String orbitalPeriod;
    private String population;
    private List<String> residents;
    private String rotationPeriod;
    private String surfaceWater;
    private String terrain;
    private String url;
}
