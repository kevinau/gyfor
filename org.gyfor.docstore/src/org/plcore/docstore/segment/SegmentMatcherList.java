package org.plcore.docstore.segment;

import java.util.List;

import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISegmentMatchResult;
import org.plcore.srcdoc.ISegmentMatcher;


public class SegmentMatcherList {

  public static ISegmentMatcher[] matchers = {
    new NamedDateMatcher(),
    new DelimitedDateMatcher(),
    new PercentMatcher(),
    new CurrencyMatcher(),
    new CentsPenceMatcher(),
    new ABNMatcher(),
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
