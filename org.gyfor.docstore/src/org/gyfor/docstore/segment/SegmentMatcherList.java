package org.gyfor.docstore.segment;

import java.util.List;

import org.gyfor.docstore.parser.ISegmentMatchResult;
import org.gyfor.docstore.parser.ISegmentMatcher;
import org.gyfor.docstore.ISegment;


public class SegmentMatcherList {

  public static ISegmentMatcher[] matchers = {
    new NamedDateMatcher(),
    new DelimitedDateMatcher(),
    new CurrencyMatcher(),
    new CentsPenceMatcher(),
    new PercentMatcher(),
  };
  
  
  public void findSpecials (List<ISegment> segments) {
    int i = 0;
    while (i < segments.size()) {
      ISegment segment = segments.get(i);
      String text = segment.getText();
      
      // Iterate through the list of matchers, looking for the first match
      findMatch (text, 0, text.length());
    }
  }
  
  
  private void findMatch (String text, int n0, int nz) {
    for (ISegmentMatcher matcher : matchers) {
      ISegmentMatchResult result = matcher.find(text, n0, nz);
      if (result != null) {
        System.out.println("<<" + text.substring(n0, nz) + ":" + result.type() + ">>");
        int n1 = result.start();
        int n2 = result.end();
        if (n0 < n1) {
          // look for matches between the start of the text and this result
          findMatch(text, n0, n1);
        }
        if (n2 < nz) {
          // look for matches between this result and the end of the text
          findMatch(text, n2, nz);
        }
        return;
      }
    }
    System.out.println("<<<" + text + ">>>");
  }
  
}
