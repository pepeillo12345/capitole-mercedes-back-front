package com.crazy.back.mapper;

import com.crazy.back.clients.swapi.response.people.SwPeopleResponse;
import com.crazy.back.dto.PeopleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PeopleMapperTest {

    private PeopleMapper peopleMapper;

    @BeforeEach
    void setUp() {
        peopleMapper = Mappers.getMapper(PeopleMapper.class);
    }

    @Test
    void shouldMapSwPeopleResponseToPeopleDto() {
        // Given
        SwPeopleResponse swPeopleResponse = new SwPeopleResponse();
        swPeopleResponse.setName("Luke Skywalker");
        swPeopleResponse.setHeight("172");
        swPeopleResponse.setMass("77");
        swPeopleResponse.setHairColor("blond");
        swPeopleResponse.setEyeColor("blue");
        swPeopleResponse.setBirthYear("19BBY");
        swPeopleResponse.setGender("male");
        swPeopleResponse.setSkinColor("fair");
        swPeopleResponse.setHomeworld("https://swapi.py4e.com/api/planets/1/");
        swPeopleResponse.setFilms(List.of("https://swapi.py4e.com/api/films/1/"));
        swPeopleResponse.setSpecies(List.of());
        swPeopleResponse.setVehicles(List.of("https://swapi.py4e.com/api/vehicles/14/"));
        swPeopleResponse.setStarships(List.of("https://swapi.py4e.com/api/starships/12/"));
        OffsetDateTime now = OffsetDateTime.now();
        swPeopleResponse.setCreated(now);
        swPeopleResponse.setEdited(now);
        swPeopleResponse.setUrl("https://swapi.py4e.com/api/people/1/");

        // When
        PeopleDto result = peopleMapper.toPeopleDto(swPeopleResponse);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Luke Skywalker");
        assertThat(result.getHeight()).isEqualTo("172");
        assertThat(result.getMass()).isEqualTo("77");
        assertThat(result.getHairColor()).isEqualTo("blond");
        assertThat(result.getEyeColor()).isEqualTo("blue");
        assertThat(result.getBirthYear()).isEqualTo("19BBY");
        assertThat(result.getGender()).isEqualTo("male");
        assertThat(result.getSkinColor()).isEqualTo("fair");
        assertThat(result.getHomeworld()).isEqualTo("https://swapi.py4e.com/api/planets/1/");
        assertThat(result.getFilms()).hasSize(1);
        assertThat(result.getFilms().getFirst()).isEqualTo("https://swapi.py4e.com/api/films/1/");
        assertThat(result.getSpecies()).isEmpty();
        assertThat(result.getVehicles()).hasSize(1);
        assertThat(result.getStarships()).hasSize(1);
        assertThat(result.getCreated()).isEqualTo(now);
        assertThat(result.getEdited()).isEqualTo(now);
        assertThat(result.getUrl()).isEqualTo("https://swapi.py4e.com/api/people/1/");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        SwPeopleResponse swPeopleResponse = new SwPeopleResponse();
        swPeopleResponse.setName("Test Person");

        // When
        PeopleDto result = peopleMapper.toPeopleDto(swPeopleResponse);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Person");
        assertThat(result.getHeight()).isNull();
        assertThat(result.getMass()).isNull();
        assertThat(result.getFilms()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        // When
        PeopleDto result = peopleMapper.toPeopleDto(null);

        // Then
        assertThat(result).isNull();
    }
}