package com.hostfully.controller.dtos;

import jakarta.validation.constraints.NotBlank;

public record PersistPropertyDto(@NotBlank(message = "name is mandatory") String name) {}
