package com.hostfully.controller.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PersistBlockDto(
    @NotBlank(message = "propertyId is mandatory") String propertyId,
    @NotNull(message = "start is mandatory") LocalDate start,
    @NotNull(message = "finish is mandatory") LocalDate finish) {}
