package com.hostfully.service;

import com.hostfully.controller.dtos.PersistReservationDto;
import com.hostfully.controller.dtos.ReservationDto;
import com.hostfully.exception.OverlappingBlocksException;
import com.hostfully.exception.OverlappingReservationsException;
import com.hostfully.exception.PropertyNotFoundException;
import com.hostfully.exception.ReservationNotFoundException;
import com.hostfully.mapper.ReservationMapper;
import com.hostfully.model.Block;
import com.hostfully.model.Property;
import com.hostfully.model.Reservation;
import com.hostfully.repository.BlockRepository;
import com.hostfully.repository.ReservationRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
@AllArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final BlockRepository blockRepository;
  private final ReservationMapper reservationMapper;

  public ReservationDto create(@Valid PersistReservationDto reservationDto) {
    Reservation toBePersisted = reservationMapper.map(reservationDto);
    toBePersisted.setProperty(new Property(reservationDto.propertyId()));

    validate(toBePersisted);

    Reservation created;
    try {
      created = reservationRepository.save(toBePersisted);
    } catch (DataIntegrityViolationException e) {
      throw new PropertyNotFoundException(reservationDto.propertyId());
    }

    return reservationMapper.map(created);
  }

  public ReservationDto findById(String reservationId) {
    return reservationMapper.map(
        reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId)));
  }

  public ReservationDto update(String reservationId, @Valid PersistReservationDto reservationDto) {
    Reservation existingReservation =
        reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));

    BeanUtils.copyProperties(reservationDto, existingReservation);
    existingReservation.setId(reservationId);

    validate(existingReservation);

    Reservation updatedReservation = reservationRepository.save(existingReservation);
    return reservationMapper.map(updatedReservation);
  }

  public void cancel(String reservationId) {
    changeStatus(reservationId, Reservation.ReservationStatus.CANCELLED);
  }

  public void rebook(String reservationId) {
    changeStatus(reservationId, Reservation.ReservationStatus.ACTIVE);
  }

  private void changeStatus(String reservationId, Reservation.ReservationStatus status) {
    Reservation existingReservation =
        reservationRepository
            .findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    existingReservation.setStatus(status);

    validate(existingReservation);

    reservationRepository.save(existingReservation);
  }

  private void validate(Reservation reservation) {
    if (reservation.getStart() != null
        && reservation.getFinish() != null
        && reservation.getStart().isAfter(reservation.getFinish())) {
      throw new IllegalArgumentException("Start cannot be after the finish");
    }

    List<Reservation> overlappingReservations =
        reservationRepository
            .findByPropertyIdAndDateRange(
                reservation.getProperty().getId(), reservation.getStart(), reservation.getFinish())
            .stream()
            .filter(b -> !b.getId().equals(reservation.getId()))
            .toList();

    if (overlappingReservations.size() > 0) {
      throw new OverlappingReservationsException(overlappingReservations);
    }

    List<Block> overlappingBlocks =
        blockRepository
            .findByPropertyIdAndDateRange(
                reservation.getProperty().getId(), reservation.getStart(), reservation.getFinish())
            .stream()
            .filter(b -> !b.getId().equals(reservation.getId()))
            .toList();

    if (overlappingBlocks.size() > 0) {
      throw new OverlappingBlocksException(overlappingBlocks);
    }
  }
}
