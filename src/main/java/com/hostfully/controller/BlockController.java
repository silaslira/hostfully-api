package com.hostfully.controller;

import com.hostfully.controller.dtos.BlockDto;
import com.hostfully.controller.dtos.PersistBlockDto;
import com.hostfully.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Block")
@RestController
@RequestMapping("/block")
@AllArgsConstructor
public class BlockController {

  private final BlockService blockService;

  @Operation(summary = "Find block by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Block found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BlockDto.class))
            }),
        @ApiResponse(responseCode = "404", description = "Block not found", content = @Content)
      })
  @GetMapping("/{blockId}")
  public BlockDto findById(
      @Parameter(description = "Id of block to be searched") @PathVariable String blockId) {
    return blockService.findById(blockId);
  }

  @Operation(summary = "Create block")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Block created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BlockDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid block passed to persist",
            content = @Content)
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BlockDto create(@RequestBody PersistBlockDto request) {
    return blockService.create(request);
  }

  @Operation(summary = "Update block")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Block updated",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BlockDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid block passed to persist",
            content = @Content)
      })
  @PutMapping("/{blockId}")
  public BlockDto update(
      @Parameter(description = "Id of block to be updated") @PathVariable String blockId,
      @RequestBody PersistBlockDto request) {
    return blockService.update(blockId, request);
  }

  @Operation(summary = "Delete block")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Block deleted",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = BlockDto.class))
            })
      })
  @DeleteMapping("/{blockId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String blockId) {
    blockService.delete(blockId);
    ;
  }
}
