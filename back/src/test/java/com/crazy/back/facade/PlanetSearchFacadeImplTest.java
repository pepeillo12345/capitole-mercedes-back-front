package com.crazy.back.facade;

import com.crazy.back.clients.swapi.feign.PlanetFeignClient;
import com.crazy.back.clients.swapi.response.SwGenericPage;
import com.crazy.back.clients.swapi.response.planet.SwPlanetResponse;
import com.crazy.back.dto.PlanetDto;
import com.crazy.back.mapper.PlanetMapper;
import com.crazy.back.util.sorting.GenericSortingFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetSearchFacadeImplTest {

    @Mock
    private PlanetFeignClient planetFeignClient;

    @Mock
    private PlanetMapper planetMapper;

    @Mock
    private GenericSortingFactory sortingFactory;

    @InjectMocks
    private PlanetSearchFacadeImpl planetSearchFacade;

    private SwPlanetResponse swPlanetResponse;
    private PlanetDto planetDto;

    @BeforeEach
    void setUp() {
        swPlanetResponse = new SwPlanetResponse();
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
        swPlanetResponse.setCreated(OffsetDateTime.now());
        swPlanetResponse.setUrl("https://swapi.py4e.com/api/planets/1/");

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
        planetDto.setUrl("https://swapi.py4e.com/api/planets/1/");
    }

    @Test
    void shouldSearchPlanetsSuccessfully() {
        // Given
        SwGenericPage<SwPlanetResponse> swPage = new SwGenericPage<>();
        swPage.setResults(List.of(swPlanetResponse));
        swPage.setCount(1);

        Pageable pageable = PageRequest.of(0, 15);

        when(planetFeignClient.getPlanetSearch(1, null)).thenReturn(swPage);
        when(planetMapper.toPlanetDto(swPlanetResponse)).thenReturn(planetDto);

        // When
        Page<PlanetDto> result = planetSearchFacade.search(null, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Tatooine");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldSearchPlanetsWithSorting() {
        // Given
        SwGenericPage<SwPlanetResponse> swPage = new SwGenericPage<>();
        swPage.setResults(List.of(swPlanetResponse));
        swPage.setCount(1);

        Pageable pageable = PageRequest.of(0, 15, Sort.by("name"));
        Comparator<PlanetDto> mockComparator = Comparator.comparing(PlanetDto::getName);

        when(planetFeignClient.getPlanetSearch(1, "Tatooine")).thenReturn(swPage);
        when(planetMapper.toPlanetDto(swPlanetResponse)).thenReturn(planetDto);
        when(sortingFactory.createComparator(eq(PlanetDto.class), any(Sort.class))).thenReturn(mockComparator);

        // When
        Page<PlanetDto> result = planetSearchFacade.search("Tatooine", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Tatooine");
    }

    @Test
    void shouldGetPlanetByIdSuccessfully() {
        // Given
        when(planetFeignClient.getPlanetById("1")).thenReturn(swPlanetResponse);
        when(planetMapper.toPlanetDto(swPlanetResponse)).thenReturn(planetDto);

        // When
        PlanetDto result = planetSearchFacade.getById("1");

        // Then
        assertThat(result.getName()).isEqualTo("Tatooine");
        assertThat(result.getClimate()).isEqualTo("arid");
    }

    @Test
    void shouldThrowExceptionWhenPlanetNotFound() {
        // Given
        when(planetFeignClient.getPlanetById("999")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> planetSearchFacade.getById("999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Planet not found with id: 999");
    }
}