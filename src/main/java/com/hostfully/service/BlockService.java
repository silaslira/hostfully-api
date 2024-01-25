package com.hostfully.service;

import com.hostfully.controller.dtos.BlockDto;
import com.hostfully.controller.dtos.PersistBlockDto;
import com.hostfully.exception.BlockNotFoundException;
import com.hostfully.exception.OverlappingBlocksException;
import com.hostfully.exception.OverlappingReservationsException;
import com.hostfully.exception.PropertyNotFoundException;
import com.hostfully.mapper.BlockMapper;
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

@Service
@AllArgsConstructor
public class BlockService {

  private final BlockRepository blockRepository;
  private final ReservationRepository reservationRepository;
  private final BlockMapper blockMapper;

  public BlockDto create(@Valid PersistBlockDto blockDto) {
    Block toBePersisted = blockMapper.map(blockDto);
    toBePersisted.setProperty(new Property(blockDto.propertyId()));

    validate(toBePersisted);

    Block created;
    try {
      created = blockRepository.save(toBePersisted);
    } catch (DataIntegrityViolationException e) {
      throw new PropertyNotFoundException(blockDto.propertyId());
    }

    return blockMapper.map(created);
  }

  public BlockDto findById(String blockId) {
    return blockMapper.map(
        blockRepository.findById(blockId).orElseThrow(() -> new BlockNotFoundException(blockId)));
  }

  public BlockDto update(String blockId, PersistBlockDto blockDto) {
    Block existingBlock =
        blockRepository.findById(blockId).orElseThrow(() -> new BlockNotFoundException(blockId));

    BeanUtils.copyProperties(blockDto, existingBlock);
    existingBlock.setId(blockId);

    validate(existingBlock);

    Block updatedBlock = blockRepository.save(existingBlock);
    return blockMapper.map(updatedBlock);
  }

  public void delete(String blockId) {
    blockRepository.deleteById(blockId);
  }

  private void validate(Block block) {
    if (block.getStart().isAfter(block.getFinish())) {
      throw new IllegalArgumentException("Start cannot be after the finish");
    }

    List<Reservation> overlappingReservations =
        reservationRepository.findByPropertyIdAndDateRange(
            block.getProperty().getId(), block.getStart(), block.getFinish());
    ;

    if (overlappingReservations.size() > 0) {
      throw new OverlappingReservationsException(overlappingReservations);
    }

    List<Block> overlappingBlocks =
        blockRepository
            .findByPropertyIdAndDateRange(
                block.getProperty().getId(), block.getStart(), block.getFinish())
            .stream()
            .filter(b -> !b.getId().equals(block.getId()))
            .toList();

    if (overlappingBlocks.size() > 0) {
      throw new OverlappingBlocksException(overlappingBlocks);
    }
  }
}
