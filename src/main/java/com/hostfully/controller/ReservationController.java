package com.hostfully.controller;

import com.hostfully.controller.dtos.PersistReservationDto;
import com.hostfully.controller.dtos.ReservationDto;
import com.hostfully.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reservation")
@RestController
@RequestMapping("/reservation")
@AllArgsConstructor
public class ReservationController {

  private final ReservationService reservationService;

  @Operation(summary = "Find reservation by id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Reservation found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ReservationDto.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Reservation not found",
            content = @Content)
      })
  @GetMapping("/{reservationId}")
  public ReservationDto findById(
      @Parameter(description = "Id of reservation to be searched") @PathVariable
          String reservationId) {
    return reservationService.findById(reservationId);
  }

  @Operation(summary = "Create reservation")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Reservation created",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ReservationDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid reservation passed to persist",
            content = @Content)
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ReservationDto create(@RequestBody PersistReservationDto request) {
    return reservationService.create(request);
  }

  @Operation(summary = "Update reservation")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Reservation updated",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ReservationDto.class))
            }),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid reservation passed to persist",
            content = @Content)
      })
  @PutMapping("/{reservationId}")
  public ReservationDto update(
      @Parameter(description = "Id of reservation to be updated") @PathVariable
          String reservationId,
      @RequestBody PersistReservationDto request) {
    return reservationService.update(reservationId, request);
  }

  @Operation(summary = "Cancel reservation")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Reservation cancelled",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ReservationDto.class))
            })
      })
  @PutMapping("/{reservationId}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(@PathVariable String reservationId) {
    reservationService.cancel(reservationId);
  }

  @Operation(summary = "Rebook reservation")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Reservation rebooked",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = ReservationDto.class))
            })
      })
  @PutMapping("/{reservationId}/rebook")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void rebook(@PathVariable String reservationId) {
    reservationService.rebook(reservationId);
  }
}
