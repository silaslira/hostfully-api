package com.hostfully.controller.dtos;

import java.util.List;

public record ErrorResponseDto(String message, List<String> errors) {}
