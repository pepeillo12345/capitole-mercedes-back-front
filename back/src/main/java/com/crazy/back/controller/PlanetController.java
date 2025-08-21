package com.crazy.back.controller;

import com.crazy.back.dto.PlanetDto;
import com.crazy.back.service.PlanetSearchServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Planets", description = "Endpoints for managing planets from the Star Wars API")
public class PlanetController {

    private final PlanetSearchServiceImpl planetService;

    @Operation(summary = "Retrieves a paginated list of planets from the Star Wars API")
    @ApiResponse(responseCode = "200", description = "List successfully retrieved")
    @GetMapping("/planets")
    public ResponseEntity<Page<PlanetDto>> getPeople(
            @Parameter(description = "Query search")
            @RequestParam(required = false) String search,
            @Parameter(description = "Pagination params")
            @PageableDefault(size = 15, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(planetService.search(search, pageable));
    }

    @Operation(summary = "Retrieves a planet by ID from the Star Wars API")
    @ApiResponse(responseCode = "200", description = "Planet retrieved successfully")
    @GetMapping("/planets/{id}")
    public ResponseEntity<PlanetDto> getPersonById(
            @Parameter(description = "ID of the planet to retrieve")
            @PathVariable("id")
            @Pattern(regexp = "\\d+", message = "ID must be a positive number")
            String id) {
        return ResponseEntity.ok(planetService.getById(id));
    }
}
