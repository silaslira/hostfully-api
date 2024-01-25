package com.hostfully.exception;

import com.hostfully.model.Reservation;

public class ReservationNotFoundException extends NotFoundException {

  public ReservationNotFoundException(String id) {
    super(Reservation.class, id);
  }
}
