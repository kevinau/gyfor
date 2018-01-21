package org.gyfor.docstore.segment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.doc.ISegmentMatchResult;
import org.gyfor.doc.ISegmentMatcher;
import org.gyfor.doc.SegmentType;


class PercentMatcher implements ISegmentMatcher {

  private final static Pattern percent = Pattern.compile("(\\d+(\\.\\d+)?)\\s?%");

  
  @Override
  public ISegmentMatchResult find(String input, int start, int end) {
    Matcher matcher = percent.matcher(input);
    matcher.region(start, end);
    
    if (matcher.find()) {
      String nnn = matcher.group(1);
      double value = Double.parseDouble(nnn) / 100.0;
      return new SegmentMatchResult(matcher, SegmentType.PERCENT, value);
    } else {
      return null;
    }
  }

}
