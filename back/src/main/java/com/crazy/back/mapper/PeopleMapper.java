package com.crazy.back.mapper;

import com.crazy.back.clients.swapi.response.people.SwPeopleResponse;
import com.crazy.back.dto.PeopleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PeopleMapper {

    PeopleDto toPeopleDto(SwPeopleResponse swPeopleResponse);

}
