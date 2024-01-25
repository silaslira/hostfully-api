package com.hostfully.controller;

import com.hostfully.controller.dtos.PersistPropertyDto;
import com.hostfully.controller.dtos.PropertyDto;
import com.hostfully.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Property")
@RestController
@RequestMapping("/property")
@AllArgsConstructor
public class PropertyController {

  private final PropertyService propertyService;

  @Operation(summary = "Get all properties available")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Properties retrieved successfully",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PropertyDto.class))
            })
      })
  @GetMapping
  public List<PropertyDto> findAll() {
    return propertyService.findAll();
  }

  @Operation(summary = "Find property by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Property found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PropertyDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Property not found", content = @Content)
      })
  @GetMapping("/{propertyId}")
  public PropertyDto findById(
      @Parameter(description = "Id of property to be searched") @PathVariable String propertyId) {
    return propertyService.findById(propertyId);
  }

  @Operation(summary = "Create property")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Property created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PropertyDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid property passed to persist",
            content = @Content)
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PropertyDto create(@RequestBody PersistPropertyDto request) {
    return propertyService.create(request);
  }

  @Operation(summary = "Update property")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Property updated",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PropertyDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid property passed to persist",
            content = @Content)
      })
  @PutMapping("/{propertyId}")
  public PropertyDto update(
      @Parameter(description = "Id of property to be updated") @PathVariable String propertyId,
      @RequestBody PersistPropertyDto request) {
    return propertyService.update(propertyId, request);
  }

  @Operation(summary = "Delete property")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Property deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = PropertyDto.class))
            })
      })
  @DeleteMapping("/{propertyId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String propertyId) {
    propertyService.delete(propertyId);
    ;
  }
}
