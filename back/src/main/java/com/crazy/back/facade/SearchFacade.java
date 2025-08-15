package com.crazy.back.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchFacade<S> {

    Page<S> search(String search, Pageable pageable);

    S getById(String id);
}
