package com.crazy.back.controller;

import com.crazy.back.dto.PeopleDto;
import com.crazy.back.service.PeopleSearchServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "People", description = "Endpoints for managing people from the Star Wars API")
public class PeopleController {

    private final PeopleSearchServiceImpl peopleService;

    @Operation(summary = "Retrieves a paginated list of people from the Star Wars API")
    @ApiResponse(responseCode = "200", description = "Listado recuperado correctamente")
    @GetMapping("/people")
    public ResponseEntity<Page<PeopleDto>> getPeople(
            @Parameter(description = "Query search")
            @RequestParam(required = false) String search,
            @Parameter(description = "Pagination params")
            @PageableDefault(size = 15, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(peopleService.search(search, pageable));
    }

    @Operation(summary = "Retrieves a person by ID from the Star Wars API")
    @ApiResponse(responseCode = "200", description = "Person retrieved successfully")
    @GetMapping("/people/{id}")
    public ResponseEntity<PeopleDto> getPersonById(
            @Parameter(description = "ID of the person to retrieve")
            @PathVariable("id") String id){
        return ResponseEntity.ok(peopleService.getById(id));
    }
}
