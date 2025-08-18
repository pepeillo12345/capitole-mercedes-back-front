package com.crazy.back.mapper;

import com.crazy.back.clients.swapi.response.planet.SwPlanetResponse;
import com.crazy.back.dto.PlanetDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlanetMapperTest {

    private PlanetMapper planetMapper;

    @BeforeEach
    void setUp() {
        planetMapper = Mappers.getMapper(PlanetMapper.class);
    }

    @Test
    void shouldMapSwPlanetResponseToPlanetDto() {
        // Given
        SwPlanetResponse swPlanetResponse = new SwPlanetResponse();
        swPlanetResponse.setName("Tatooine");
        swPlanetResponse.setRotationPeriod("23");
        swPlanetResponse.setOrbitalPeriod("304");
        swPlanetResponse.setDiameter("10465");
        swPlanetResponse.setClimate("arid");
        swPlanetResponse.setGravity("1 standard");
        swPlanetResponse.setTerrain("desert");
        swPlanetResponse.setSurfaceWater("1");
        swPlanetResponse.setPopulation("200000");
        swPlanetResponse.setResidents(List.of("https://swapi.py4e.com/api/people/1/"));
        swPlanetResponse.setFilms(List.of("https://swapi.py4e.com/api/films/1/"));
        OffsetDateTime now = OffsetDateTime.now();
        swPlanetResponse.setCreated(now);
        swPlanetResponse.setEdited(now);
        swPlanetResponse.setUrl("https://swapi.py4e.com/api/planets/1/");

        // When
        PlanetDto result = planetMapper.toPlanetDto(swPlanetResponse);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Tatooine");
        assertThat(result.getRotationPeriod()).isEqualTo("23");
        assertThat(result.getOrbitalPeriod()).isEqualTo("304");
        assertThat(result.getDiameter()).isEqualTo("10465");
        assertThat(result.getClimate()).isEqualTo("arid");
        assertThat(result.getGravity()).isEqualTo("1 standard");
        assertThat(result.getTerrain()).isEqualTo("desert");
        assertThat(result.getSurfaceWater()).isEqualTo("1");
        assertThat(result.getPopulation()).isEqualTo("200000");
        assertThat(result.getResidents()).hasSize(1);
        assertThat(result.getResidents().getFirst()).isEqualTo("https://swapi.py4e.com/api/people/1/");
        assertThat(result.getFilms()).hasSize(1);
        assertThat(result.getFilms().getFirst()).isEqualTo("https://swapi.py4e.com/api/films/1/");
        assertThat(result.getCreated()).isEqualTo(now);
        assertThat(result.getEdited()).isEqualTo(now);
        assertThat(result.getUrl()).isEqualTo("https://swapi.py4e.com/api/planets/1/");
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        SwPlanetResponse swPlanetResponse = new SwPlanetResponse();
        swPlanetResponse.setName("Test Planet");

        // When
        PlanetDto result = planetMapper.toPlanetDto(swPlanetResponse);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Planet");
        assertThat(result.getClimate()).isNull();
        assertThat(result.getDiameter()).isNull();
        assertThat(result.getFilms()).isNull();
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        // When
        PlanetDto result = planetMapper.toPlanetDto(null);

        // Then
        assertThat(result).isNull();
    }
}