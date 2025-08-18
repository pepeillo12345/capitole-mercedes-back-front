package com.crazy.back.service;

import com.crazy.back.dto.PlanetDto;
import com.crazy.back.facade.PlanetSearchFacadeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetSearchServiceImplTest {

    @Mock
    private PlanetSearchFacadeImpl planetFacade;

    @InjectMocks
    private PlanetSearchServiceImpl planetService;

    private PlanetDto planetDto;

    @BeforeEach
    void setUp() {
        planetDto = new PlanetDto();
        planetDto.setName("Tatooine");
        planetDto.setRotationPeriod("23");
        planetDto.setOrbitalPeriod("304");
        planetDto.setDiameter("10465");
        planetDto.setClimate("arid");
        planetDto.setGravity("1 standard");
        planetDto.setTerrain("desert");
        planetDto.setSurfaceWater("1");
        planetDto.setPopulation("200000");
        planetDto.setResidents(List.of("https://swapi.py4e.com/api/people/1/"));
        planetDto.setFilms(List.of("https://swapi.py4e.com/api/films/1/"));
        planetDto.setCreated(OffsetDateTime.now());
        planetDto.setEdited(OffsetDateTime.now());
        planetDto.setUrl("https://swapi.py4e.com/api/planets/1/");
    }

    @Test
    void shouldSearchPlanetsSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Page<PlanetDto> expectedPage = new PageImpl<>(List.of(planetDto), pageable, 1);

        when(planetFacade.search("Tatooine", pageable)).thenReturn(expectedPage);

        // When
        Page<PlanetDto> result = planetService.search("Tatooine", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Tatooine");
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(planetFacade).search("Tatooine", pageable);
    }

    @Test
    void shouldSearchPlanetsWithNullSearch() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Page<PlanetDto> expectedPage = new PageImpl<>(List.of(planetDto), pageable, 1);

        when(planetFacade.search(null, pageable)).thenReturn(expectedPage);

        // When
        Page<PlanetDto> result = planetService.search(null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Tatooine");
        verify(planetFacade).search(null, pageable);
    }

    @Test
    void shouldGetPlanetByIdSuccessfully() {
        // Given
        when(planetFacade.getById("1")).thenReturn(planetDto);

        // When
        PlanetDto result = planetService.getById("1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Tatooine");
        assertThat(result.getClimate()).isEqualTo("arid");
        assertThat(result.getDiameter()).isEqualTo("10465");
        verify(planetFacade).getById("1");
    }

    @Test
    void shouldReturnEmptyPageWhenNoResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Page<PlanetDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(planetFacade.search("nonexistent", pageable)).thenReturn(emptyPage);

        // When
        Page<PlanetDto> result = planetService.search("nonexistent", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(planetFacade).search("nonexistent", pageable);
    }
}