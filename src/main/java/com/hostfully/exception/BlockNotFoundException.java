package com.hostfully.exception;

import com.hostfully.model.Block;

public class BlockNotFoundException extends NotFoundException {

  public BlockNotFoundException(String id) {
    super(Block.class, id);
  }
}
