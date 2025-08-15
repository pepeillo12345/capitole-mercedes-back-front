package com.crazy.back.mapper;

import com.crazy.back.clients.swapi.response.planet.SwPlanetResponse;
import com.crazy.back.dto.PlanetDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanetMapper {

    PlanetDto toPlanetDto(SwPlanetResponse swPlanetResponse);

}
