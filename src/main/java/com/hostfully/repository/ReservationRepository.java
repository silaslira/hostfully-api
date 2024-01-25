package com.hostfully.repository;

import com.hostfully.model.Reservation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

  @Query(
      "SELECT r FROM Reservation r "
          + "WHERE r.property.id = :propertyId "
          + "AND r.status = com.hostfully.model.Reservation$ReservationStatus.ACTIVE "
          + "AND ( "
          + "   :startDate BETWEEN r.start AND r.finish  OR "
          + "   :endDate BETWEEN r.start AND r.finish OR "
          + "   r.start BETWEEN :startDate AND :endDate  OR "
          + "   r.finish BETWEEN :startDate AND :endDate "
          + ") ")
  List<Reservation> findByPropertyIdAndDateRange(
      @Param("propertyId") String propertyId,
      @Param("startDate") LocalDate start,
      @Param("endDate") LocalDate finish);
}
