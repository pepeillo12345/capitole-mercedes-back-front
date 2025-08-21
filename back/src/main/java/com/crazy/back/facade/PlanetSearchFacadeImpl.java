package com.crazy.back.facade;

import com.crazy.back.clients.swapi.feign.PlanetFeignClient;
import com.crazy.back.clients.swapi.response.planet.SwPlanetResponse;
import com.crazy.back.dto.PlanetDto;
import com.crazy.back.exception.ResourceNotFoundException;
import com.crazy.back.mapper.PlanetMapper;
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
public class PlanetSearchFacadeImpl implements SearchFacade<PlanetDto> {
    private final PlanetFeignClient planetFeignClient;
    private final PlanetMapper planetMapper;
    private final GenericSortingFactory sortingFactory;

    @Override
    public Page<PlanetDto> search(String search, Pageable pageable) {
        try {
            log.debug("Searching planets with query: {} and pageable: {}", search, pageable);

            // Get data without sorting
            Page<SwPlanetResponse> swPlanetResponse = CompositePaginationHelper.fetchAndCompose(
                    10,
                    pageable,
                    swapiPage -> planetFeignClient.getPlanetSearch(swapiPage, search)
            );

            // Convert to DTO
            List<PlanetDto> planetDtos = swPlanetResponse.getContent()
                    .stream()
                    .map(planetMapper::toPlanetDto)
                    .collect(Collectors.toList());

            // Apply sorting if needed
            if (pageable.getSort().isSorted()) {
                log.debug("Applying sorting: {}", pageable.getSort());
                Comparator<PlanetDto> comparator = sortingFactory.createComparator(PlanetDto.class, pageable.getSort());
                if (comparator != null) {
                    planetDtos.sort(comparator);
                }
            }

            log.debug("Successfully retrieved {} planets out of {} total",
                    planetDtos.size(), swPlanetResponse.getTotalElements());

            return new PageImpl<>(planetDtos, pageable, swPlanetResponse.getTotalElements());

        } catch (Exception ex) {
            log.error("Error searching planets with query: '{}', page: {}, size: {}",
                    search, pageable.getPageNumber(), pageable.getPageSize(), ex);
            throw ex;
        }
    }

    @Override
    public PlanetDto getById(String id) {
        log.debug("Getting planet by id: {}", id);

        try {
            return Optional.ofNullable(planetFeignClient.getPlanetById(id))
                    .map(response -> {
                        log.debug("Successfully retrieved planet: {}", response.getName());
                        return planetMapper.toPlanetDto(response);
                    })
                    .orElseThrow(() -> {
                        log.warn("Planet not found with id: {}", id);
                        return new ResourceNotFoundException("Planet not found with id: " + id);
                    });

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error getting planet by id: {}", id, ex);
            throw ex;
        }
    }
}
