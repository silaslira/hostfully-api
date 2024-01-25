package com.hostfully.controller.dtos;

import java.util.List;

public record PropertyDto(
    String id, String name, List<BlockDto> blocks, List<ReservationDto> reservations) {}
