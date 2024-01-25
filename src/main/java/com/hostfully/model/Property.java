package com.hostfully.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Property {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private String id;

  @NotBlank(message = "Name is mandatory")
  private String name;

  @OneToMany(mappedBy = "property")
  private List<Block> blocks = new ArrayList<>();

  @OneToMany(mappedBy = "property")
  private List<Reservation> reservations = new ArrayList<>();

  public Property(String id) {
    this.id = id;
  }
}
