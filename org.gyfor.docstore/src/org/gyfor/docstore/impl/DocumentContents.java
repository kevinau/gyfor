package org.gyfor.docstore.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gyfor.docstore.Dictionary;
import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.ISegment;
import org.gyfor.docstore.SegmentType;
import org.gyfor.docstore.parser.ISegmentMatchResult;
import org.gyfor.docstore.parser.ISegmentMatcher;
import org.gyfor.docstore.segment.SegmentMatcherList;

import com.sleepycat.persist.model.Persistent;


@Persistent
public class DocumentContents implements IDocumentContents, Serializable {

  private static final long serialVersionUID = 1L;
  
  private final List<ISegment> segments;
  
  
  public DocumentContents () {
    segments = new ArrayList<>();
  }
  
  
  private DocumentContents (int n) {
    segments = new ArrayList<>(n);
  }
  
  
  void add(PartialSegment partialSegment) {
    if (partialSegment == null) {
      throw new IllegalArgumentException("Segment argument cannot be null");
    }
    // Iterate through the list of matchers, looking for the first match
    String word = partialSegment.getWord();
    findMatch (partialSegment, word, 0, word.length());
  }

 
  private void findMatch (PartialSegment partialSegment, String text, int n0, int nz) {
    ISegment s = null;
    
    ISegmentMatcher[] matchers = SegmentMatcherList.matchers;
    for (ISegmentMatcher matcher : matchers) {
      ISegmentMatchResult result = matcher.find(text, n0, nz);
      if (result != null) {
        int n1 = result.start();
        int n2 = result.end();
        if (n0 < n1) {
          // look for matches between the start of the text and this result
          findMatch(partialSegment, text, n0, n1);
        }
        
        float x0 = partialSegment.adjustedX1(n1);
        float y0 = partialSegment.getY0();
        float x1 = partialSegment.adjustedX1(n2);
        float y1 = partialSegment.getY1();
        s = new Segment(x0, y0, x1, y1, text.substring(n1, n2), result.type(), result.value());
        segments.add(s);
        
        if (n2 < nz) {
          // look for matches between this result and the end of the text
          findMatch(partialSegment, text, n2, nz);
        }
        return;
      }
    }
    
    String t = text.substring(n0, nz);
    if (!ISegment.isDiscardable(t)) {
      float x0 = partialSegment.adjustedX1(n0);
      float y0 = partialSegment.getY0();
      float x1 = partialSegment.adjustedX1(nz);
      float y1 = partialSegment.getY1();
      s = new Segment(x0, y0, x1, y1, t, SegmentType.TEXT, null);
      segments.add(s);
    }
  }


  @Override
  public List<? extends ISegment> getSegments () {
    return segments;
  }
  
  
  @Override
  public int size () {
    return segments.size();
  }
  
  
  @Override
  public void scaleSegments (double factor) {
    if (factor != 1.0) {
      for (ISegment segment : segments) {
        segment.scaleBoundingBox(factor);
      }
    }
  }
  
  
  @Override
  public IDocumentContents merge (IDocumentContents other) {
    if (other.size() == 0) {
      return this;
    }
    if (this.size() == 0) {
      return other;
    }
    
    DocumentContents newDocContents = new DocumentContents(this.size() + other.size());
    List<ISegment> mergedSegments = newDocContents.segments;
    List<? extends ISegment> segments2 = other.getSegments();
    int i = 0;
    int j = 0;
    while (i < segments.size() && j < segments2.size()) {
      ISegment seg1 = segments.get(i);
      ISegment seg2 = segments2.get(j);
      int n = seg1.compareTo(seg2);
      if (n < 0) {
        mergedSegments.add(seg1);
        i++;
      } else if (n > 0) {
        mergedSegments.add(seg2);
        j++;
      } else {
        mergedSegments.add(seg1);
        mergedSegments.add(seg2);
        i++;
        j++;
      }
    }
    while (i < segments.size()) {
      ISegment seg1 = segments.get(i);
      mergedSegments.add(seg1);
      i++;
    }
    while (j < segments2.size()) {
      ISegment seg2 = segments2.get(j);
      mergedSegments.add(seg2);
      j++;
    }
    return newDocContents;
  }
  
  
  @Override
  public void sortSegments () {
    Collections.sort(segments);
  }
  
  
  void resolveWordIndexs (Dictionary dictionary) {
    for (ISegment segment : segments) {
      segment.resolveWordIndex(dictionary);
    }
  }

  
  //void setWordIndexs (Dictionary dictionary) {
  //  for (Segment segment : segments) {
  //    segment.setWordIndex(dictionary);
  //  }
  //}

  
  @Override
  public void updateDictionary (Dictionary dictionary) {
    for (ISegment segment : segments) {
      segment.updateDictionary(dictionary);
    }
    ////dictionary.updateWordCounts();
  }

  
//  @Override
//  public Instance newInstance (double classIndex, Dictionary dictionary, int numAttributes) {
//    int dictSize = dictionary.size();
//    int[] docCounts = new int[dictSize];
//    for (ISegment segment : segments) {
//      int n = dictionary.getWordIndex(segment.getText());
//       if (n != -1) {
//        docCounts[n]++;
//      }
//    }
//
//    int[] indexes = new int[dictSize + 1];
//    double[] values = new double[dictSize + 1];
//
//    indexes[0] = 0;
//    values[0] = classIndex;
//    
//    // Instance counts
//    ////int[] dictCounts = dictionary.getDictionaryWordCounts();
//    ////double[] values2 = new double[dictSize + 1];
//    ////values2[0] = classIndex;
//    
//    int m = 1;
//    int mm = 1;
//    ////int nn = 1;
//    for (int j = 0; j < dictSize; j++) {
//      ////int dictCount = dictCounts[j];
//      ////if (dictCount >= 2) {
//        ////values2[nn++] = docCounts[j];
//        if (docCounts[j] > 0) {
//          indexes[m] = mm;
//          values[m] = docCounts[j];
//          m++;
//        }
//        mm++;
//      ////}
//    }
//    
//    int[] indexes4 = Arrays.copyOf(indexes, m);
//    double[] values4 = Arrays.copyOf(values, m);
//    
//    //return new SparseInstance(1.0, values2, indexes2, dictSize + 1);
//    //validate (values4, indexes4, mm, dictionary, numAttributes);
//    Instance sinstance = new SparseInstance(1.0, values4, indexes4, dictSize + 1);
//    //System.out.println("New instance " + sinstance);
//    return sinstance;
//  }


  @Override
  public void dump () {
    System.out.println("Segments: " + segments.size());
    for (ISegment segment : segments) {
      System.out.println(segment);
    }
    System.out.println();
  }

}
