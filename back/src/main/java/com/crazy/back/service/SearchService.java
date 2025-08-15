package com.crazy.back.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * SearchService interface for handling search operations.
 * This interface can be extended to define methods for searching entities.
 */
public interface SearchService<S> {
    Page<S> search(String search, Pageable pageable);

    S getById(String id);
}
