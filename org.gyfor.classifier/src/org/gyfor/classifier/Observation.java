package org.gyfor.classifier;

import java.util.List;

import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.gyfor.docstore.Document;
import org.gyfor.docstore.ISegment;
import org.gyfor.docstore.SegmentType;


public class Observation {

  private static final int MIN_WORD_LENGTH = 3;
  private static final double CUTOFF_POINT = 0.25;
  
  private final String name;
  private DenseVector vector;
  private int actual;


  public Observation(Document doc, int actual, WordDictionary dictionary) {
    // For training, get the partyName (for the moment)
//    String partyName = doc.getOriginName().substring(0, 3);
//    if (!partyNames.contains(partyName)) {
//      partyNames.add(partyName);
//      System.out.println(partyName);
//    }
//    int partyIndex = partyNames.indexOf(partyName);
    this.name = doc.getId();
    
    // Count characters in first page
    int charCount = 0;
    List<? extends ISegment> segments = doc.getContents().getSegments();
//    Comparator<ISegment> keyComparator = new Comparator<ISegment>() {
//      @Override
//      public int compare(ISegment arg0, ISegment arg1) {
//        // Descending order of height
//        float h0 = arg0.getHeight();
//        float h1 = arg1.getHeight();
//        if (h0 > h1) {
//          return +1;
//        } else if (h0 < h1) {
//          return -1;
//        } else {
//          return 0;
//        }
//      }
//    };
//    Collections.sort(segments, keyComparator);

    
    for (ISegment segment : segments) {
      if (segment.getPageIndex() == 0) {
        charCount += segment.getText().length();
      }
    }
    int charCutoff = (int)(charCount * CUTOFF_POINT);
    
    // Build the array of "words"
    charCount = 0;
    vector = new DenseVector(dictionary.size() + 1);
    vector.set(0, 1);
    for (ISegment segment : segments) {
      if (segment.getPageIndex() > 0) {
        // Only look at the first page
        break;
      }
      if (segment.getType() == SegmentType.TEXT) {
        String word = segment.getText().trim();
        if (word.length() >= MIN_WORD_LENGTH) {
          int wi = dictionary.getWordIndex(word);
          vector.set(wi + 1, 1);
        }
      }
      charCount += segment.getText().length();
      if (charCount > charCutoff) {
        break;
      }
    }
    this.actual = actual;
  }


  public String getName() {
    return name;
  }
  
  
  public Vector getVector() {
    return vector;
  }


  public int getActual() {
    return actual;
  }
}
