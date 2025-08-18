package com.crazy.back.util.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class PageableKeyGeneratorTest {

    private PageableKeyGenerator keyGenerator;
    private Method mockMethod;
    private Object mockTarget;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        keyGenerator = new PageableKeyGenerator();
        mockTarget = new Object();
        mockMethod = Object.class.getMethod("toString");
    }

    @Test
    void shouldGenerateKeyForPageableWithoutSearch() {
        // Given
        String search = null;
        Pageable pageable = PageRequest.of(0, 15);

        // When
        Object key = keyGenerator.generate(mockTarget, mockMethod, search, pageable);

        // Then
        assertThat(key).isEqualTo("0_15_UNSORTED_all");
    }

    @Test
    void shouldGenerateKeyForPageableWithSearch() {
        // Given
        String search = "Luke";
        Pageable pageable = PageRequest.of(1, 20);

        // When
        Object key = keyGenerator.generate(mockTarget, mockMethod, search, pageable);

        // Then
        assertThat(key).isEqualTo("1_20_UNSORTED_Luke");
    }

    @Test
    void shouldGenerateKeyForPageableWithSort() {
        // Given
        String search = "Skywalker";
        Pageable pageable = PageRequest.of(2, 10, Sort.by("name"));

        // When
        Object key = keyGenerator.generate(mockTarget, mockMethod, search, pageable);

        // Then
        assertThat(key).isEqualTo("2_10_name: ASC_Skywalker");
    }

    @Test
    void shouldGenerateKeyForPageableWithMultipleSort() {
        // Given
        String search = "test";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name").and(Sort.by(Sort.Direction.DESC, "created")));

        // When
        Object key = keyGenerator.generate(mockTarget, mockMethod, search, pageable);

        // Then
        assertThat(key).isEqualTo("0_5_name: ASC,created: DESC_test");
    }

    @Test
    void shouldGenerateKeyForEmptySearch() {
        // Given
        String search = "";
        Pageable pageable = PageRequest.of(3, 25);

        // When
        Object key = keyGenerator.generate(mockTarget, mockMethod, search, pageable);

        // Then
        assertThat(key).isEqualTo("3_25_UNSORTED_");
    }

    @Test
    void shouldGenerateDifferentKeysForDifferentParameters() {
        // Given
        Pageable pageable1 = PageRequest.of(0, 15);
        Pageable pageable2 = PageRequest.of(1, 15);
        Pageable pageable3 = PageRequest.of(0, 20);

        // When
        Object key1 = keyGenerator.generate(mockTarget, mockMethod, "test", pageable1);
        Object key2 = keyGenerator.generate(mockTarget, mockMethod, "test", pageable2);
        Object key3 = keyGenerator.generate(mockTarget, mockMethod, "test", pageable3);
        Object key4 = keyGenerator.generate(mockTarget, mockMethod, "other", pageable1);

        // Then
        assertThat(key1).isNotEqualTo(key2);
        assertThat(key1).isNotEqualTo(key3);
        assertThat(key1).isNotEqualTo(key4);
        assertThat(key2).isNotEqualTo(key3);
    }

    @Test
    void shouldGenerateSameKeyForSameParameters() {
        // Given
        String search = "Luke";
        Pageable pageable = PageRequest.of(0, 15, Sort.by("name"));

        // When
        Object key1 = keyGenerator.generate(mockTarget, mockMethod, search, pageable);
        Object key2 = keyGenerator.generate(mockTarget, mockMethod, search, pageable);

        // Then
        assertThat(key1).isEqualTo(key2);
    }
}