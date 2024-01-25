package com.hostfully.mapper;

import com.hostfully.controller.dtos.PersistPropertyDto;
import com.hostfully.controller.dtos.PropertyDto;
import com.hostfully.model.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DomainPropertyMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "blocks", ignore = true)
  @Mapping(target = "reservations", ignore = true)
  Property map(PersistPropertyDto propertyDto);

  PropertyDto map(Property created);
}
