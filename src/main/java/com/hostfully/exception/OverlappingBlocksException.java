package com.hostfully.exception;

import com.hostfully.model.Block;
import java.util.List;
import java.util.stream.Collectors;

public class OverlappingBlocksException extends IllegalArgumentException {
  public OverlappingBlocksException(List<Block> overlappingBlocks) {
    super(
        "Selected range is overlapping with previously defined block(s): "
            + overlappingBlocks.stream()
                .map(
                    b -> String.format("(%s: %s until %s)", b.getId(), b.getStart(), b.getFinish()))
                .collect(Collectors.joining(", ")));
  }
}
