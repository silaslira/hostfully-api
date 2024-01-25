package com.hostfully.service;

import com.hostfully.controller.dtos.PersistPropertyDto;
import com.hostfully.controller.dtos.PropertyDto;
import com.hostfully.exception.PropertyNotFoundException;
import com.hostfully.mapper.DomainPropertyMapper;
import com.hostfully.model.Property;
import com.hostfully.repository.PropertyRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PropertyService {

  private final PropertyRepository propertyRepository;
  private final DomainPropertyMapper domainPropertyMapper;

  public List<PropertyDto> findAll() {
    return propertyRepository.findAll().stream().map(domainPropertyMapper::map).toList();
  }

  public PropertyDto create(PersistPropertyDto propertyDto) {
    Property created = propertyRepository.save(domainPropertyMapper.map(propertyDto));
    return domainPropertyMapper.map(created);
  }

  public PropertyDto findById(String propertyId) {
    return domainPropertyMapper.map(
        propertyRepository
            .findById(propertyId)
            .orElseThrow(() -> new PropertyNotFoundException(propertyId)));
  }

  public PropertyDto update(String propertyId, PersistPropertyDto propertyDto) {
    Property existingProperty =
        propertyRepository
            .findById(propertyId)
            .orElseThrow(() -> new PropertyNotFoundException(propertyId));

    BeanUtils.copyProperties(propertyDto, existingProperty);
    existingProperty.setId(propertyId);

    Property updatedProperty = propertyRepository.save(existingProperty);
    return domainPropertyMapper.map(updatedProperty);
  }

  public void delete(String propertyId) {
    propertyRepository.deleteById(propertyId);
  }
}
