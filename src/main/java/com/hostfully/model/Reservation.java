package com.hostfully.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Reservation {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private String id;

  @ManyToOne private Property property;

  @NotNull(message = "Start is mandatory") private LocalDate start;

  @NotNull(message = "Finish is mandatory") private LocalDate finish;

  @NotBlank(message = "Guest name is mandatory")
  private String guestName;

  private ReservationStatus status = ReservationStatus.ACTIVE;

  public enum ReservationStatus {
    ACTIVE,
    CANCELLED
  }
}
