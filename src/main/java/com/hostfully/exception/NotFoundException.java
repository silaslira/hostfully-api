package com.hostfully.exception;

public abstract class NotFoundException extends RuntimeException {

  public NotFoundException(Class<?> clazz, String id) {
    super(
        String.format("Could not find %s for the id: %s", clazz.getSimpleName().toLowerCase(), id));
  }
}
