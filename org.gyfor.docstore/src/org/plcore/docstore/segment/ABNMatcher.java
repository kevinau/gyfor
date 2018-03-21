package org.plcore.docstore.segment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.plcore.srcdoc.ISegmentMatchResult;
import org.plcore.srcdoc.ISegmentMatcher;
import org.plcore.srcdoc.SegmentType;


class ABNMatcher implements ISegmentMatcher {

  private final static Pattern abnDigits = Pattern.compile("(A\\.?B\\.?N\\.?\\:?(\\s)*)?([1-9]\\d \\d{3} \\d{3} \\d{3})");


  @Override
  public ISegmentMatchResult find(String input, int start, int end) {
    Matcher matcher = abnDigits.matcher(input);
    matcher.region(start, end);

    if (matcher.find()) {
      String nn = matcher.group(3);
      if (isValidABN(nn)) {
        return new SegmentMatchResult(matcher, SegmentType.COMPANY_NUMBER, nn);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }


  /**
   * Test if the string argument is a valid ABN. It is assumed that the string
   * argument is of the form "nn nnn nnn nnn".
   */
  private static boolean isValidABN(String abn) {
    int[] weights = { 10, 1, -1, 3, 5, 7, -1, 9, 11, 13, -1, 15, 17, 19 };

    // First digit
    int digit = abn.charAt(0) - '0' - 1;
    int sum = weights[0] * digit;
    
    // Subsequent digits
    for (int i = 1; i < abn.length(); ++i) {
      int weight = weights[i];
      if (weight != -1) {
        digit = abn.charAt(i) - '0';
        sum += weight * digit;
      }
    }
    return sum % 89 == 0;
  }
}
