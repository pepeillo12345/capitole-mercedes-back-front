package com.crazy.back.clients.swapi.feign.error;

import com.crazy.back.exception.ExternalApiException;
import com.crazy.back.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SwapiErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("SWAPI Error: {} - {} for method: {}",
                response.status(), response.reason(), methodKey);

        return switch (response.status()) {
            case 404 -> new ResourceNotFoundException(
                    String.format("Resource not found in SWAPI: %s", extractResourceInfo(methodKey))
            );
            case 429 -> new ExternalApiException(
                    "Rate limit exceeded for SWAPI",
                    response.status(),
                    "SWAPI"
            );
            case 500, 502, 503, 504 -> new ExternalApiException(
                    "SWAPI service temporarily unavailable",
                    response.status(),
                    "SWAPI"
            );
            default -> new ExternalApiException(
                    String.format("Unexpected error from SWAPI: %s", response.reason()),
                    response.status(),
                    "SWAPI"
            );
        };
    }

    private String extractResourceInfo(String methodKey) {
        if (methodKey.contains("getPeopleById")) {
            return "Person";
        } else if (methodKey.contains("getPlanetById")) {
            return "Planet";
        } else if (methodKey.contains("getPeopleSearch")) {
            return "People search results";
        } else if (methodKey.contains("getPlanetSearch")) {
            return "Planet search results";
        }
        return "Resource";
    }
}
