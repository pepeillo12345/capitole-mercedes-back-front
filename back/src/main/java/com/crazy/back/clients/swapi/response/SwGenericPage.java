package com.crazy.back.clients.swapi.response;

import lombok.Data;

import java.util.List;

/**
 * Represents a generic paginated response from the Star Wars API.
 * This class can be used to handle various types of paginated responses.
 *
 * @param <S> the type of results contained in the page
 */
@Data
public class SwGenericPage<S> {
    private int count;
    private String next;
    private String previous;
    private List<S> results;
}
