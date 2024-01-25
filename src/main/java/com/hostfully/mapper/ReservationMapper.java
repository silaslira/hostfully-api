package com.hostfully.mapper;

import com.hostfully.controller.dtos.PersistReservationDto;
import com.hostfully.controller.dtos.ReservationDto;
import com.hostfully.model.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

  ReservationDto map(Reservation reservation);

  Reservation map(PersistReservationDto reservation);
}
