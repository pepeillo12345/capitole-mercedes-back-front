package com.crazy.back.service;

import com.crazy.back.dto.PlanetDto;
import com.crazy.back.facade.PlanetSearchFacadeImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanetSearchServiceImpl implements SearchService<PlanetDto> {

    private final PlanetSearchFacadeImpl planetFacade;

    @Override
    @Cacheable(value = "planet", keyGenerator = "pageableKeyGenerator")
    public Page<PlanetDto> search(String search, Pageable pageable) {
        return planetFacade.search(search, pageable);
    }

    @Override
    @Cacheable(value = "planetById", key = "#id")
    public PlanetDto getById(String id) {
        return planetFacade.getById(id);
    }
}
