package com.crazy.back.service;

import com.crazy.back.dto.PeopleDto;
import com.crazy.back.facade.PeopleSearchFacadeImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PeopleSearchServiceImpl implements SearchService<PeopleDto> {

    private final PeopleSearchFacadeImpl peopleFacade;

    @Override
    @Cacheable(value = "people", keyGenerator = "pageableKeyGenerator")
    public Page<PeopleDto> search(String search, Pageable pageable) {
        return peopleFacade.search(search, pageable);
    }

    @Override
    @Cacheable(value = "peopleById", key = "#id")
    public PeopleDto getById(String id) {
        return peopleFacade.getById(id);
    }
}
