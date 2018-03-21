package org.plcore.docstore.segment;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.plcore.srcdoc.ISegmentMatchResult;
import org.plcore.srcdoc.ISegmentMatcher;
import org.plcore.srcdoc.SegmentType;


class NamedDateMatcher implements ISegmentMatcher {

  private final static Pattern namedMonthDate = Pattern.compile("(\\d{1,2})(st|nd|rd|th|ST|ND|RD|TH)?\\s?(\\p{Alpha}+),?\\s?(\\d{4}|\\s?\\d{2})");
  
  private final static Map<String, Integer> monthNames;
  
  static {
    monthNames = new HashMap<>(12 * 6);
    
    String[] longMonths = new DateFormatSymbols().getMonths();
    String[] shortMonths = new DateFormatSymbols().getShortMonths();
    for (int i = 0; i < 12; i++) {
      String longMonth = longMonths[i];
      String shortMonth = shortMonths[i];
      int mm = i + 1;
      monthNames.put(longMonth, mm);
      monthNames.put(shortMonth, mm);
      monthNames.put(longMonth.toLowerCase(), mm);
      monthNames.put(shortMonth.toLowerCase(), mm);
      monthNames.put(longMonth.toUpperCase(), mm);
      monthNames.put(shortMonth.toUpperCase(), mm);
    }
  }
  
  
  @Override
  public ISegmentMatchResult find(String input, int start, int end) {
    Matcher matcher = namedMonthDate.matcher(input);
    matcher.region(start, end);
    
    if (matcher.find()) {
      String dd = matcher.group(1);
      int dayNum = Integer.parseInt(dd);
      // Approximate dd check
      if (dayNum < 1 || dayNum > 31) {
        return null;
      }
      
      String ordinal = matcher.group(2);
      if (ordinal != null && ordinal.length() > 0) {
        switch (dayNum) {
        case 1 :
        case 21 :
        case 31 :
          if (ordinal.equals("st") || ordinal.equals("ST")) {
            break;  
          }
          return null;
        case 2 :
        case 22 :
          if (ordinal.equals("nd") || ordinal.equals("ND")) {
            break;  
          }
          return null;
        case 3 :
        case 23 :
          if (ordinal.equals("rd") || ordinal.equals("RD")) {
            break;  
          }
          return null;
        default :
          if (ordinal.equals("th") || ordinal.equals("TH")) {
            break;  
          }
          return null;
        }
      }
      String month = matcher.group(3);
      Integer monthNum = monthNames.get(month);
      if (monthNum == null) {
        return null;
      }
      // Matcher group(4) is the year
      String yyyy = matcher.group(4);
      int yearNum = Integer.parseInt(yyyy);
      LocalDate date = LocalDate.of(yearNum, monthNum, dayNum);
      return new SegmentMatchResult(matcher, SegmentType.DATE, date);
    } else {
      return null;
    }
  }
  
}
