package com.crazy.back.clients.swapi.response;

import lombok.Data;

import java.util.List;

@Data
public class SwGenericPage<S> {
    private int count;
    private String next;
    private String previous;
    private List<S> results;
}
