package com.hostfully.controller.dtos;

import java.time.LocalDate;

public record ReservationDto(
    String id, LocalDate start, LocalDate finish, String guestName, String status) {}
