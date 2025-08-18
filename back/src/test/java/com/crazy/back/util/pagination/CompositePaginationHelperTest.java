package com.crazy.back.util.pagination;

import com.crazy.back.clients.swapi.response.SwGenericPage;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class CompositePaginationHelperTest {

    @Test
    void shouldFetchFirstPageCorrectly() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Function<Integer, SwGenericPage<String>> fetchFunction = createMockFetchFunction();

        // When
        Page<String> result = CompositePaginationHelper.fetchAndCompose(10, pageable, fetchFunction);

        // Then
        assertThat(result.getContent()).hasSize(15);
        assertThat(result.getContent().get(0)).isEqualTo("item_0");
        assertThat(result.getContent().get(14)).isEqualTo("item_14");
        assertThat(result.getTotalElements()).isEqualTo(100);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(15);
    }

    @Test
    void shouldFetchSecondPageCorrectly() {
        // Given
        Pageable pageable = PageRequest.of(1, 15);
        Function<Integer, SwGenericPage<String>> fetchFunction = createMockFetchFunction();

        // When
        Page<String> result = CompositePaginationHelper.fetchAndCompose(10, pageable, fetchFunction);

        // Then
        assertThat(result.getContent()).hasSize(15);
        assertThat(result.getContent().get(0)).isEqualTo("item_15");
        assertThat(result.getContent().get(14)).isEqualTo("item_29");
        assertThat(result.getTotalElements()).isEqualTo(100);
    }

    @Test
    void shouldHandlePageSpanningMultipleExternalPages() {
        // Given - Página que requiere elementos de múltiples páginas externas
        Pageable pageable = PageRequest.of(2, 15); // elementos 30-44, span páginas 3-4 de SWAPI

        Function<Integer, SwGenericPage<String>> fetchFunction = createMockFetchFunction();

        // When
        Page<String> result = CompositePaginationHelper.fetchAndCompose(10, pageable, fetchFunction);

        // Then
        assertThat(result.getContent()).hasSize(15);
        assertThat(result.getContent().get(0)).isEqualTo("item_30");
        assertThat(result.getContent().get(14)).isEqualTo("item_44");
    }

    @Test
    void shouldHandleLastPageWithFewerElements() {
        // Given
        Pageable pageable = PageRequest.of(6, 15); // elementos 90-104, pero solo hay 100 elementos
        Function<Integer, SwGenericPage<String>> fetchFunction = createMockFetchFunction();

        // When
        Page<String> result = CompositePaginationHelper.fetchAndCompose(10, pageable, fetchFunction);

        // Then
        assertThat(result.getContent()).hasSize(10); // Solo 10 elementos disponibles
        assertThat(result.getContent().get(0)).isEqualTo("item_90");
        assertThat(result.getContent().get(9)).isEqualTo("item_99");
        assertThat(result.getTotalElements()).isEqualTo(100);
    }

    @Test
    void shouldHandleSmallPageSize() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Function<Integer, SwGenericPage<String>> fetchFunction = createMockFetchFunction();

        // When
        Page<String> result = CompositePaginationHelper.fetchAndCompose(10, pageable, fetchFunction);

        // Then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getContent().get(0)).isEqualTo("item_0");
        assertThat(result.getContent().get(4)).isEqualTo("item_4");
    }

    @Test
    void shouldHandleEmptyResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 15);
        Function<Integer, SwGenericPage<String>> fetchFunction = page -> {
            SwGenericPage<String> response = new SwGenericPage<>();
            response.setResults(List.of());
            response.setCount(0);
            response.setNext(null);
            return response;
        };

        // When
        Page<String> result = CompositePaginationHelper.fetchAndCompose(10, pageable, fetchFunction);

        // Then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    void shouldHandleOffsetInFirstExternalPage() {
        // Given - Página que empieza en medio de la primera página externa
        Pageable pageable = PageRequest.of(0, 5);
        Function<Integer, SwGenericPage<String>> fetchFunction = page -> {
            SwGenericPage<String> response = new SwGenericPage<>();
            // Primera página externa tiene elementos 5-14 (simulando offset de 5)
            response.setResults(List.of("item_5", "item_6", "item_7", "item_8", "item_9",
                    "item_10", "item_11", "item_12", "item_13", "item_14"));
            response.setCount(20);
            response.setNext("page2");
            return response;
        };

        // When
        Page<String> result = CompositePaginationHelper.fetchAndCompose(10, pageable, fetchFunction);

        // Then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getContent().get(0)).isEqualTo("item_5");
        assertThat(result.getContent().get(4)).isEqualTo("item_9");
    }

    private Function<Integer, SwGenericPage<String>> createMockFetchFunction() {
        return page -> {
            SwGenericPage<String> response = new SwGenericPage<>();

            int startIndex = (page - 1) * 10; // páginas de SWAPI empiezan en 1
            int endIndex = Math.min(startIndex + 10, 100); // máximo 100 elementos

            List<String> items = List.of();
            if (startIndex < 100) {
                items = java.util.stream.IntStream.range(startIndex, endIndex)
                        .mapToObj(i -> "item_" + i)
                        .toList();
            }

            response.setResults(items);
            response.setCount(100);
            response.setNext(endIndex < 100 ? "page" + (page + 1) : null);

            return response;
        };
    }
}