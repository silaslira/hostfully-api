package com.hostfully.controller;

import com.hostfully.controller.dtos.ErrorResponseDto;
import com.hostfully.exception.NotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleConstraintViolation(
      ConstraintViolationException ex) {
    List<String> errors =
        ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
    return new ResponseEntity<>(
        new ErrorResponseDto("Invalid object", errors), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handlePropertyNotFoundException(NotFoundException ex) {
    return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage(), null), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    return new ResponseEntity<>(
        new ErrorResponseDto(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
  }
}
