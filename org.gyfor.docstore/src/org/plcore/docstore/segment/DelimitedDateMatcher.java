package org.plcore.docstore.segment;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.plcore.srcdoc.ISegmentMatchResult;
import org.plcore.srcdoc.ISegmentMatcher;
import org.plcore.srcdoc.SegmentType;


class DelimitedDateMatcher implements ISegmentMatcher {

  private final static Pattern slashedDate = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4}|\\d{2})");

  @Override
  public ISegmentMatchResult find(String input, int start, int end) {
    Matcher matcher = slashedDate.matcher(input);
    matcher.region(start, end);
    
    if (matcher.find()) {
      String dd = matcher.group(1);
      int dayNum = Integer.parseInt(dd);
      // Approximate dd check
      if (dayNum < 1 || dayNum > 31) {
        return null;
      }
      
      String mm = matcher.group(2);
      int monthNum = Integer.parseInt(mm);
      if (monthNum < 1 || monthNum > 12) {
        return null;
      }

      // Matcher group(3) is a two or four digit year
      String yyyy = matcher.group(3);
      int yearNum = Integer.parseInt(yyyy);
      LocalDate date = LocalDate.of(yearNum, monthNum, dayNum);
      return new SegmentMatchResult(matcher, SegmentType.DATE, date);
    } else {
      return null;
    }
  }

}
