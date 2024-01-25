package com.hostfully.exception;

import com.hostfully.model.Property;

public class PropertyNotFoundException extends NotFoundException {

  public PropertyNotFoundException(String id) {
    super(Property.class, id);
  }
}
