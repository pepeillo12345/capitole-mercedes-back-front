package com.crazy.back.facade;

import com.crazy.back.clients.swapi.feign.PeopleFeignClient;
import com.crazy.back.clients.swapi.response.people.SwPeopleResponse;
import com.crazy.back.dto.PeopleDto;
import com.crazy.back.mapper.PeopleMapper;
import com.crazy.back.util.pagination.CompositePaginationHelper;
import com.crazy.back.util.sorting.GenericSortingFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PeopleSearchFacadeImpl implements SearchFacade<PeopleDto> {
    private final PeopleFeignClient peopleFeignClient;
    private final PeopleMapper peopleMapper;
    private final GenericSortingFactory sortingFactory;

    public Page<PeopleDto> search(String search, Pageable pageable) {
        // Obtenemos los datos sin sorting
        Page<SwPeopleResponse> swPeopleResponse = CompositePaginationHelper.fetchAndCompose(
                10,
                pageable,
                swapiPage -> peopleFeignClient.getPeopleSearch(swapiPage, search)
        );

        // Convertimos a DTO
        List<PeopleDto> peopleDtos = swPeopleResponse.getContent()
                .stream()
                .map(peopleMapper::toPeopleDto)
                .collect(Collectors.toList());

        // Aplicamos sorting si es necesario
        if (pageable.getSort().isSorted()) {
            Comparator<PeopleDto> comparator = sortingFactory.createComparator(PeopleDto.class, pageable.getSort());
            peopleDtos.sort(comparator);
        }

        return new PageImpl<>(peopleDtos, pageable, swPeopleResponse.getTotalElements());
    }


    public PeopleDto getById(String id) {
        return Optional.ofNullable(peopleFeignClient.getPeopleById(id))
                .map(peopleMapper::toPeopleDto)
                .orElseThrow(() -> new RuntimeException("People not found with id: " + id));
    }
}
