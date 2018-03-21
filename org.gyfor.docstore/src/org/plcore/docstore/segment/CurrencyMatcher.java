package org.plcore.docstore.segment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.plcore.srcdoc.ISegmentMatchResult;
import org.plcore.srcdoc.ISegmentMatcher;
import org.plcore.srcdoc.SegmentType;
import org.plcore.math.Decimal;

class CurrencyMatcher implements ISegmentMatcher {

   private final static String moneyPattern = "-?(\\d{1,3}(,\\d{3})+|\\d*)\\.\\d\\d(?!\\d)";

  /** 
   * Dollar sign currency, marked as a prefix: $, $X, X$, where X is A or NZ (with optional spaces).
   */
  private final static Pattern dollarPattern = Pattern.compile("(\\$\\s?(A|NZ|US|CA)\\s?|(A|NZ|US|CA)\\s?\\$\\s?|\\$\\s?)" + moneyPattern);
  

  /** 
   * UK pound sign currency, marked as a prefix: £, £UK, UK£ (with optional spaces).
   */
  private final static Pattern poundPattern = Pattern.compile("(\\£\\s?(UK)\\s?|(UK)\\s?\\£\\s?|\\£\\s?)" + moneyPattern);
  

  /** 
   * Euro sign currency, marked as a prefix: €.
   */
  private final static Pattern euroPattern = Pattern.compile("€\\s?" + moneyPattern);
  

  /** 
   * ISO 4217 currency, marked as a prefix: AUD, NZD, GBP, EUR, USD or CAD.
   */
  private final static Pattern isoPattern = Pattern.compile("(AUD|NZD|GBP|EUR|USD|CAD)\\s?" + moneyPattern);
  
  /** 
   * Non-country currency.
   */
  private final static Pattern localPattern = Pattern.compile(moneyPattern);
  

  @Override
  public ISegmentMatchResult find(String input, int start, int end) {
    Pattern pattern;
    int n = input.indexOf('$', start);
    if (n != -1 && n < end) {
      pattern = dollarPattern;
    } else {
      n = input.indexOf('£', start);
      if (n != -1 && n < end) {
        pattern = poundPattern;
      } else {
        n = input.indexOf('€', start);
        if (n != -1 && n < end) {
          pattern = euroPattern;
        } else if (Character.isUpperCase(input.charAt(start))) {
          pattern = isoPattern;
        } else {
          pattern = localPattern;
        }
      }
    }
    Matcher matcher = pattern.matcher(input);
    matcher.region(start, end);
    
    if (matcher.find()) {
      Decimal value = null;
      for (int i = matcher.start(); i < matcher.end(); i++) {
        char c = input.charAt(i);
        if (Character.isDigit(c) || c == '-') {
          value = new Decimal(input.substring(i, matcher.end()));
          break;
        }
      }
      return new SegmentMatchResult(matcher, SegmentType.CURRENCY, value);
    } else {
      return null;
    }
  }

}
