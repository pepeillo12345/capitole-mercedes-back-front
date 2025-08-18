package com.crazy.back.controller;

import com.crazy.back.dto.PeopleDto;
import com.crazy.back.service.PeopleSearchServiceImpl;
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

@WebMvcTest(PeopleController.class)
class PeopleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PeopleSearchServiceImpl peopleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetPeopleSuccessfully() throws Exception {
        PeopleDto person = createTestPerson();
        Page<PeopleDto> page = new PageImpl<>(List.of(person), PageRequest.of(0, 15), 1);

        when(peopleService.search(eq(null), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/people")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Luke Skywalker"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldGetPeopleWithSearchParameter() throws Exception {
        PeopleDto person = createTestPerson();
        Page<PeopleDto> page = new PageImpl<>(List.of(person), PageRequest.of(0, 15), 1);

        when(peopleService.search(eq("Luke"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/people")
                        .param("search", "Luke")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Luke Skywalker"));
    }

    @Test
    void shouldGetPersonById() throws Exception {
        PeopleDto person = createTestPerson();

        when(peopleService.getById("1")).thenReturn(person);

        mockMvc.perform(get("/api/v1/people/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Luke Skywalker"))
                .andExpect(jsonPath("$.height").value("172"));
    }

    private PeopleDto createTestPerson() {
        PeopleDto person = new PeopleDto();
        person.setName("Luke Skywalker");
        person.setHeight("172");
        person.setMass("77");
        person.setHairColor("blond");
        person.setEyeColor("blue");
        person.setBirthYear("19BBY");
        person.setGender("male");
        person.setHomeworld("https://swapi.py4e.com/api/planets/1/");
        person.setFilms(List.of("https://swapi.py4e.com/api/films/1/"));
        person.setSpecies(List.of());
        person.setVehicles(List.of());
        person.setStarships(List.of());
        person.setCreated(OffsetDateTime.now());
        person.setEdited(OffsetDateTime.now());
        person.setUrl("https://swapi.py4e.com/api/people/1/");
        return person;
    }
}