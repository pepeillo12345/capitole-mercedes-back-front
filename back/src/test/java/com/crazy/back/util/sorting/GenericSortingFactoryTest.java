package com.crazy.back.util.sorting;

import com.crazy.back.dto.PeopleDto;
import com.crazy.back.dto.PlanetDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenericSortingFactoryTest {

    private GenericSortingFactory sortingFactory;

    @BeforeEach
    void setUp() {
        sortingFactory = new GenericSortingFactory();
    }

    @Test
    void shouldValidateSortableFields() {
        // Valid sortable fields
        assertThat(sortingFactory.isValidSortField(PeopleDto.class, "name")).isTrue();
        assertThat(sortingFactory.isValidSortField(PeopleDto.class, "created")).isTrue();
        assertThat(sortingFactory.isValidSortField(PlanetDto.class, "name")).isTrue();
        assertThat(sortingFactory.isValidSortField(PlanetDto.class, "created")).isTrue();

        // Invalid sortable fields
        assertThat(sortingFactory.isValidSortField(PeopleDto.class, "height")).isFalse();
        assertThat(sortingFactory.isValidSortField(PeopleDto.class, "mass")).isFalse();
        assertThat(sortingFactory.isValidSortField(PlanetDto.class, "climate")).isFalse();
        assertThat(sortingFactory.isValidSortField(PlanetDto.class, "nonexistent")).isFalse();
    }

    @Test
    void shouldCreateStringComparator() {
        // Given
        Sort sort = Sort.by("name");
        List<PeopleDto> people = createTestPeople();

        // When
        Comparator<PeopleDto> comparator = sortingFactory.createComparator(PeopleDto.class, sort);
        people.sort(comparator);

        // Then
        assertThat(people.get(0).getName()).isEqualTo("Anakin Skywalker");
        assertThat(people.get(1).getName()).isEqualTo("Luke Skywalker");
        assertThat(people.get(2).getName()).isEqualTo("Obi-Wan Kenobi");
    }

    @Test
    void shouldCreateStringComparatorDescending() {
        // Given
        Sort sort = Sort.by(Sort.Direction.DESC, "name");
        List<PeopleDto> people = createTestPeople();

        // When
        Comparator<PeopleDto> comparator = sortingFactory.createComparator(PeopleDto.class, sort);
        people.sort(comparator);

        // Then
        assertThat(people.get(0).getName()).isEqualTo("Obi-Wan Kenobi");
        assertThat(people.get(1).getName()).isEqualTo("Luke Skywalker");
        assertThat(people.get(2).getName()).isEqualTo("Anakin Skywalker");
    }

    @Test
    void shouldCreateDateComparator() {
        // Given
        Sort sort = Sort.by("created");
        List<PeopleDto> people = createTestPeopleWithDates();

        // When
        Comparator<PeopleDto> comparator = sortingFactory.createComparator(PeopleDto.class, sort);
        people.sort(comparator);

        // Then
        assertThat(people.get(0).getName()).isEqualTo("Old Person");
        assertThat(people.get(1).getName()).isEqualTo("Middle Person");
        assertThat(people.get(2).getName()).isEqualTo("New Person");
    }

    @Test
    void shouldHandleNullValuesInComparison() {
        // Given
        Sort sort = Sort.by("name");
        List<PeopleDto> people = new ArrayList<>();

        PeopleDto person1 = new PeopleDto();
        person1.setName("Luke");

        PeopleDto person2 = new PeopleDto();
        person2.setName(null);

        PeopleDto person3 = new PeopleDto();
        person3.setName("Anakin");

        people.add(person1);
        people.add(person2);
        people.add(person3);

        // When
        Comparator<PeopleDto> comparator = sortingFactory.createComparator(PeopleDto.class, sort);
        people.sort(comparator);

        // Then
        assertThat(people.get(0).getName()).isNull(); // nulls first
        assertThat(people.get(1).getName()).isEqualTo("Anakin");
        assertThat(people.get(2).getName()).isEqualTo("Luke");
    }

    @Test
    void shouldThrowExceptionForNonSortableField() {
        // Given
        Sort sort = Sort.by("height");

        // When & Then
        assertThatThrownBy(() -> sortingFactory.createComparator(PeopleDto.class, sort))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Field 'height' is not sortable for PeopleDto");
    }

    @Test
    void shouldThrowExceptionForNonExistentField() {
        // Given
        Sort sort = Sort.by("nonexistent");

        // When & Then
        assertThatThrownBy(() -> sortingFactory.createComparator(PeopleDto.class, sort))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Field 'nonexistent' is not sortable for PeopleDto");
    }

    private List<PeopleDto> createTestPeople() {
        List<PeopleDto> people = new ArrayList<>();

        PeopleDto luke = new PeopleDto();
        luke.setName("Luke Skywalker");

        PeopleDto anakin = new PeopleDto();
        anakin.setName("Anakin Skywalker");

        PeopleDto obiWan = new PeopleDto();
        obiWan.setName("Obi-Wan Kenobi");

        people.add(luke);
        people.add(anakin);
        people.add(obiWan);

        return people;
    }

    private List<PeopleDto> createTestPeopleWithDates() {
        List<PeopleDto> people = new ArrayList<>();

        PeopleDto oldPerson = new PeopleDto();
        oldPerson.setName("Old Person");
        oldPerson.setCreated(OffsetDateTime.now().minusDays(10));

        PeopleDto newPerson = new PeopleDto();
        newPerson.setName("New Person");
        newPerson.setCreated(OffsetDateTime.now());

        PeopleDto middlePerson = new PeopleDto();
        middlePerson.setName("Middle Person");
        middlePerson.setCreated(OffsetDateTime.now().minusDays(5));

        people.add(newPerson);
        people.add(oldPerson);
        people.add(middlePerson);

        return people;
    }
}