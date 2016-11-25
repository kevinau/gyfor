package org.gyfor.docstore.segment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.docstore.SegmentType;
import org.gyfor.docstore.parser.ISegmentMatchResult;
import org.gyfor.docstore.parser.ISegmentMatcher;
import org.gyfor.math.Decimal;

class CentsPenceMatcher implements ISegmentMatcher {

  private final static Pattern centsPence = Pattern.compile("([1-9]\\d*)\\s+([Cc]ents|[Pp]ence)");

  @Override
  public ISegmentMatchResult find(String input, int start, int end) {
    Matcher matcher = centsPence.matcher(input);
    matcher.region(start, end);
    
    if (matcher.find()) {
      String nn = matcher.group(1);
      Decimal value = new Decimal(nn).divide(100);
      return new SegmentMatchResult(matcher, SegmentType.CURRENCY, value);
    } else {
      return null;
    }
  }

}
