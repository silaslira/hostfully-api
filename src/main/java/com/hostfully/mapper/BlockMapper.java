package com.hostfully.mapper;

import com.hostfully.controller.dtos.BlockDto;
import com.hostfully.controller.dtos.PersistBlockDto;
import com.hostfully.model.Block;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlockMapper {

  BlockDto map(Block block);

  Block map(PersistBlockDto block);
}
