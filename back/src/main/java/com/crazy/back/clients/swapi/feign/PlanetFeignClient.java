package com.crazy.back.clients.swapi.feign;

import com.crazy.back.clients.swapi.response.SwGenericPage;
import com.crazy.back.clients.swapi.response.people.SwPeopleResponse;
import com.crazy.back.clients.swapi.response.planet.SwPlanetResponse;
import com.crazy.back.config.SwFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "sw-planet-client",
        url = "${clients.external.swapi.url}",
        configuration = SwFeignConfig.class)
public interface PlanetFeignClient {

    @GetMapping("/planets/{id}")
    SwPlanetResponse getPlanetById(@PathVariable("id") String id);

    @GetMapping("/planets/")
    SwGenericPage<SwPlanetResponse> getPlanetSearch(
            @RequestParam("page") int page,
            @RequestParam(value = "search", required = false) String search
    );
}
