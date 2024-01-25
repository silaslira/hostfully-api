package com.hostfully.exception;

import com.hostfully.model.Reservation;
import java.util.List;
import java.util.stream.Collectors;

public class OverlappingReservationsException extends IllegalArgumentException {
  public OverlappingReservationsException(List<Reservation> overlappingReservation) {
    super(
        "Selected range is overlapping with previously defined reservation(s): "
            + overlappingReservation.stream()
                .map(
                    r -> String.format("(%s: %s until %s)", r.getId(), r.getStart(), r.getFinish()))
                .collect(Collectors.joining(", ")));
  }
}
