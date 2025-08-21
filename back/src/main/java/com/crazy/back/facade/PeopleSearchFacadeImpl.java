package com.crazy.back.facade;

import com.crazy.back.clients.swapi.feign.PeopleFeignClient;
import com.crazy.back.clients.swapi.response.people.SwPeopleResponse;
import com.crazy.back.dto.PeopleDto;
import com.crazy.back.exception.ResourceNotFoundException;
import com.crazy.back.mapper.PeopleMapper;
import com.crazy.back.util.pagination.CompositePaginationHelper;
import com.crazy.back.util.sorting.GenericSortingFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PeopleSearchFacadeImpl implements SearchFacade<PeopleDto> {
    private final PeopleFeignClient peopleFeignClient;
    private final PeopleMapper peopleMapper;
    private final GenericSortingFactory sortingFactory;


    @Override
    public Page<PeopleDto> search(String search, Pageable pageable) {
        try {
            log.debug("Searching people with query: {} and pageable: {}", search, pageable);

            // Get the data without sorting
            Page<SwPeopleResponse> swPeopleResponse = CompositePaginationHelper.fetchAndCompose(
                    10,
                    pageable,
                    swapiPage -> peopleFeignClient.getPeopleSearch(swapiPage, search)
            );

            // Convert to dto
            List<PeopleDto> peopleDtos = swPeopleResponse.getContent()
                    .stream()
                    .map(peopleMapper::toPeopleDto)
                    .collect(Collectors.toList());

            // Apply sorting if needed
            if (pageable.getSort().isSorted()) {
                log.debug("Applying sorting: {}", pageable.getSort());
                Comparator<PeopleDto> comparator = sortingFactory.createComparator(PeopleDto.class, pageable.getSort());
                if (comparator != null) {
                    peopleDtos.sort(comparator);
                }
            }

            log.debug("Successfully retrieved {} people out of {} total",
                    peopleDtos.size(), swPeopleResponse.getTotalElements());

            return new PageImpl<>(peopleDtos, pageable, swPeopleResponse.getTotalElements());

        } catch (Exception ex) {
            log.error("Error searching people with query: '{}', page: {}, size: {}",
                    search, pageable.getPageNumber(), pageable.getPageSize(), ex);
            throw ex;
        }
    }

    @Override
    public PeopleDto getById(String id) {
        log.debug("Getting person by id: {}", id);

        try {
            return Optional.ofNullable(peopleFeignClient.getPeopleById(id))
                    .map(response -> {
                        log.debug("Successfully retrieved person: {}", response.getName());
                        return peopleMapper.toPeopleDto(response);
                    })
                    .orElseThrow(() -> {
                        log.warn("Person not found with id: {}", id);
                        return new ResourceNotFoundException("Person not found with id: " + id);
                    });

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error getting person by id: {}", id, ex);
            throw ex;
        }
    }
}
