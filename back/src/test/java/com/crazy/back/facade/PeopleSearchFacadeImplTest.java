package com.crazy.back.facade;

import com.crazy.back.clients.swapi.feign.PeopleFeignClient;
import com.crazy.back.clients.swapi.response.SwGenericPage;
import com.crazy.back.clients.swapi.response.people.SwPeopleResponse;
import com.crazy.back.dto.PeopleDto;
import com.crazy.back.mapper.PeopleMapper;
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
class PeopleSearchFacadeImplTest {

    @Mock
    private PeopleFeignClient peopleFeignClient;

    @Mock
    private PeopleMapper peopleMapper;

    @Mock
    private GenericSortingFactory sortingFactory;

    @InjectMocks
    private PeopleSearchFacadeImpl peopleSearchFacade;

    private SwPeopleResponse swPeopleResponse;
    private PeopleDto peopleDto;

    @BeforeEach
    void setUp() {
        swPeopleResponse = new SwPeopleResponse();
        swPeopleResponse.setName("Luke Skywalker");
        swPeopleResponse.setHeight("172");
        swPeopleResponse.setMass("77");
        swPeopleResponse.setHairColor("blond");
        swPeopleResponse.setEyeColor("blue");
        swPeopleResponse.setBirthYear("19BBY");
        swPeopleResponse.setGender("male");
        swPeopleResponse.setHomeworld("https://swapi.py4e.com/api/planets/1/");
        swPeopleResponse.setFilms(List.of("https://swapi.py4e.com/api/films/1/"));
        swPeopleResponse.setCreated(OffsetDateTime.now());
        swPeopleResponse.setUrl("https://swapi.py4e.com/api/people/1/");

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
        peopleDto.setCreated(OffsetDateTime.now());
        peopleDto.setUrl("https://swapi.py4e.com/api/people/1/");
    }

    @Test
    void shouldSearchPeopleSuccessfully() {
        // Given
        SwGenericPage<SwPeopleResponse> swPage = new SwGenericPage<>();
        swPage.setResults(List.of(swPeopleResponse));
        swPage.setCount(1);

        Pageable pageable = PageRequest.of(0, 15);

        when(peopleFeignClient.getPeopleSearch(1, null)).thenReturn(swPage);
        when(peopleMapper.toPeopleDto(swPeopleResponse)).thenReturn(peopleDto);

        // When
        Page<PeopleDto> result = peopleSearchFacade.search(null, pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Luke Skywalker");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void shouldSearchPeopleWithSorting() {
        // Given
        SwGenericPage<SwPeopleResponse> swPage = new SwGenericPage<>();
        swPage.setResults(List.of(swPeopleResponse));
        swPage.setCount(1);

        Pageable pageable = PageRequest.of(0, 15, Sort.by("name"));
        Comparator<PeopleDto> mockComparator = Comparator.comparing(PeopleDto::getName);

        when(peopleFeignClient.getPeopleSearch(1, "Luke")).thenReturn(swPage);
        when(peopleMapper.toPeopleDto(swPeopleResponse)).thenReturn(peopleDto);
        when(sortingFactory.createComparator(eq(PeopleDto.class), any(Sort.class))).thenReturn(mockComparator);

        // When
        Page<PeopleDto> result = peopleSearchFacade.search("Luke", pageable);

        // Then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Luke Skywalker");
    }

    @Test
    void shouldGetPersonByIdSuccessfully() {
        // Given
        when(peopleFeignClient.getPeopleById("1")).thenReturn(swPeopleResponse);
        when(peopleMapper.toPeopleDto(swPeopleResponse)).thenReturn(peopleDto);

        // When
        PeopleDto result = peopleSearchFacade.getById("1");

        // Then
        assertThat(result.getName()).isEqualTo("Luke Skywalker");
        assertThat(result.getHeight()).isEqualTo("172");
    }

    @Test
    void shouldThrowExceptionWhenPersonNotFound() {
        // Given
        when(peopleFeignClient.getPeopleById("999")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> peopleSearchFacade.getById("999"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("People not found with id: 999");
    }
}