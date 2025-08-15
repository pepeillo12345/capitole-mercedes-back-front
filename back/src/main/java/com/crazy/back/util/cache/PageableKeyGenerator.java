package com.crazy.back.util.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * https://www.baeldung.com/spring-cache-custom-keygenerator
 * TL;DR: Cache key generator for not typing the same search query multiple times.
 * PageableKeyGenerator is a custom key generator for caching pageable search results.
 * It generates a unique cache key based on the page number, page size, sort order, and search term.
 */
@Component
public class PageableKeyGenerator implements KeyGenerator {
    @Override
    public @NonNull Object generate(@NonNull Object target, @NonNull Method method, @NonNull Object... params) {
        String search = (String) params[0];
        Pageable pageable = (Pageable) params[1];

        return String.format("%d_%d_%s_%s",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort(),
                search != null ? search : "all"
        );
    }
}
