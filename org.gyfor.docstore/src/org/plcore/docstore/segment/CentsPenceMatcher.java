package org.plcore.docstore.segment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.plcore.math.Decimal;
import org.plcore.srcdoc.ISegmentMatchResult;
import org.plcore.srcdoc.ISegmentMatcher;
import org.plcore.srcdoc.SegmentType;


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
