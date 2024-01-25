package com.hostfully.repository;

import com.hostfully.model.Block;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends JpaRepository<Block, String> {

  @Query(
      "SELECT b FROM Block b "
          + "WHERE b.property.id = :propertyId "
          + "AND ( "
          + "   :startDate BETWEEN b.start AND b.finish  OR "
          + "   :endDate BETWEEN b.start AND b.finish OR "
          + "   b.start BETWEEN :startDate AND :endDate  OR "
          + "   b.finish BETWEEN :startDate AND :endDate "
          + ")")
  List<Block> findByPropertyIdAndDateRange(
      @Param("propertyId") String propertyId,
      @Param("startDate") LocalDate start,
      @Param("endDate") LocalDate finish);
}
