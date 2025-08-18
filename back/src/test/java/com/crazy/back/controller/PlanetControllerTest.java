package com.crazy.back.controller;

import com.crazy.back.dto.PlanetDto;
import com.crazy.back.service.PlanetSearchServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlanetController.class)
class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanetSearchServiceImpl planetService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetPlanetsSuccessfully() throws Exception {
        PlanetDto planet = createTestPlanet();
        Page<PlanetDto> page = new PageImpl<>(List.of(planet), PageRequest.of(0, 15), 1);

        when(planetService.search(eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/planets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Tatooine"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldGetPlanetsWithSearchParameter() throws Exception {
        PlanetDto planet = createTestPlanet();
        Page<PlanetDto> page = new PageImpl<>(List.of(planet), PageRequest.of(0, 15), 1);

        when(planetService.search(eq("Tatooine"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/planets")
                        .param("search", "Tatooine")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Tatooine"));
    }

    @Test
    void shouldGetPlanetById() throws Exception {
        PlanetDto planet = createTestPlanet();

        when(planetService.getById("1")).thenReturn(planet);

        mockMvc.perform(get("/api/v1/planets/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tatooine"))
                .andExpect(jsonPath("$.climate").value("arid"));
    }

    private PlanetDto createTestPlanet() {
        PlanetDto planet = new PlanetDto();
        planet.setName("Tatooine");
        planet.setRotationPeriod("23");
        planet.setOrbitalPeriod("304");
        planet.setDiameter("10465");
        planet.setClimate("arid");
        planet.setGravity("1 standard");
        planet.setTerrain("desert");
        planet.setSurfaceWater("1");
        planet.setPopulation("200000");
        planet.setResidents(List.of("https://swapi.py4e.com/api/people/1/"));
        planet.setFilms(List.of("https://swapi.py4e.com/api/films/1/"));
        planet.setCreated(OffsetDateTime.now());
        planet.setEdited(OffsetDateTime.now());
        planet.setUrl("https://swapi.py4e.com/api/planets/1/");
        return planet;
    }
}