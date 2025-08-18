package com.crazy.back.service;

import com.crazy.back.dto.PeopleDto;
import com.crazy.back.facade.PeopleSearchFacadeImpl;
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
class PeopleSearchServiceImplTest {

    @Mock
    private PeopleSearchFacadeImpl peopleFacade;

    @InjectMocks
    private PeopleSearchServiceImpl peopleService;

    private PeopleDto peopleDto;

    @BeforeEach
    void setUp() {
        peopleDto = new PeopleDto();
        peopleDto.setName("Luke Skywalker");
        peopleDto.setHeight("172");
        peopleDto.setMass("77");
        peopleDto.setHairColor("blond");
        peopleDto.setEyeColor("blue");
        peopleDto.setBirthYear("19BBY");
        peopleDto.setGender("male");
        peopleDto.setHomeworld("https://swapi.py4e.com/api/planets/1/");
        peopleDto.setFilms(List.of("https://swapi.py4e.com/api/films/1/"));
        peopleDto.setSpecies(List.of());
        peopleDto.setVehicles(List.of());
        peopleDto.setStarships(List.of());
        peopleDto.setCreated(OffsetDateTime.now());
        peopleDto.setEdited(OffsetDateTime.now());
        peopleDto.setUrl("https://swapi.py4e.com/api/people/1/");
    }

    @Test
    void shouldSearchPeopleSuccessfully() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Page<PeopleDto> expectedPage = new PageImpl<>(List.of(peopleDto), pageable, 1);

        when(peopleFacade.search("Luke", pageable)).thenReturn(expectedPage);

        // When
        Page<PeopleDto> result = peopleService.search("Luke", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Luke Skywalker");
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(peopleFacade).search("Luke", pageable);
    }

    @Test
    void shouldSearchPeopleWithNullSearch() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Page<PeopleDto> expectedPage = new PageImpl<>(List.of(peopleDto), pageable, 1);

        when(peopleFacade.search(null, pageable)).thenReturn(expectedPage);

        // When
        Page<PeopleDto> result = peopleService.search(null, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Luke Skywalker");
        verify(peopleFacade).search(null, pageable);
    }

    @Test
    void shouldGetPersonByIdSuccessfully() {
        // Given
        when(peopleFacade.getById("1")).thenReturn(peopleDto);

        // When
        PeopleDto result = peopleService.getById("1");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Luke Skywalker");
        assertThat(result.getHeight()).isEqualTo("172");
        assertThat(result.getMass()).isEqualTo("77");
        verify(peopleFacade).getById("1");
    }

    @Test
    void shouldReturnEmptyPageWhenNoResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Page<PeopleDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(peopleFacade.search("nonexistent", pageable)).thenReturn(emptyPage);

        // When
        Page<PeopleDto> result = peopleService.search("nonexistent", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(peopleFacade).search("nonexistent", pageable);
    }
}