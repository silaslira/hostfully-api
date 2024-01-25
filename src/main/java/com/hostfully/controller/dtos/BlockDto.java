package com.hostfully.controller.dtos;

import java.time.LocalDate;

public record BlockDto(String id, LocalDate start, LocalDate finish) {}
