package com.crazy.back.facade;

import com.crazy.back.clients.swapi.feign.PlanetFeignClient;
import com.crazy.back.clients.swapi.response.planet.SwPlanetResponse;
import com.crazy.back.dto.PlanetDto;
import com.crazy.back.mapper.PlanetMapper;
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
public class PlanetSearchFacadeImpl implements SearchFacade<PlanetDto> {
    private final PlanetFeignClient planetFeignClient;
    private final PlanetMapper planetMapper;
    private final GenericSortingFactory sortingFactory;

    public Page<PlanetDto> search(String search, Pageable pageable) {
        //Get data without sorting
        Page<SwPlanetResponse> swPlanetResponse = CompositePaginationHelper.fetchAndCompose(
                10,
                pageable,
                swapiPage -> planetFeignClient.getPlanetSearch(swapiPage, search)
        );

        //Conver to DTO
        List<PlanetDto> planetDtos = swPlanetResponse.getContent()
                .stream()
                .map(planetMapper::toPlanetDto)
                .collect(Collectors.toList());

        //Apply sorting if needed
        if (pageable.getSort().isSorted()) {
            Comparator<PlanetDto> comparator = sortingFactory.createComparator(PlanetDto.class, pageable.getSort());
            planetDtos.sort(comparator);
        }

        return new PageImpl<>(planetDtos, pageable, swPlanetResponse.getTotalElements());
    }

    public PlanetDto getById(String id) {
        return Optional.ofNullable(planetFeignClient.getPlanetById(id))
                .map(planetMapper::toPlanetDto)
                .orElseThrow(() -> new RuntimeException("Planet not found with id: " + id));
    }
}
