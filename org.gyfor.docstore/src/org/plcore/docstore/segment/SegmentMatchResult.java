package org.plcore.docstore.segment;

import java.util.regex.MatchResult;

import org.plcore.srcdoc.ISegmentMatchResult;
import org.plcore.srcdoc.SegmentType;


class SegmentMatchResult implements ISegmentMatchResult {

  private final int start;
  private final int end;
  private final SegmentType type;
  private final Object value;
  
  SegmentMatchResult (int start, int end, SegmentType type, Object value) {
    this.start = start;
    this.end = end;
    this.type = type;
    this.value = value;
  }
  
  
  SegmentMatchResult (MatchResult matchResult, SegmentType type, Object value) {
    this.start = matchResult.start();
    this.end = matchResult.end();
    this.type = type;
    this.value = value;
  }
  
  
  @Override
  public int start() {
    return start;
  }

  @Override
  public int end() {
    return end;
  }

  @Override
  public SegmentType type() {
    return type;
  }

  @Override
  public Object value() {
    return value;
  }
  
}
