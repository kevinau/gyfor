package org.gyfor.classifier.impl;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.classifier.IDocumentClassifier;
import org.gyfor.doc.Dictionary;
import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.ISegment;
import org.gyfor.doc.SegmentType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;


@Component(configurationPolicy=ConfigurationPolicy.REQUIRE)
public class DocumentClassifier implements IDocumentClassifier {

  private Dictionary dictionary = new Dictionary();
  private List<CategoryData> categories = new ArrayList<>();
  
  
  private String agressiveTrim(String t) {
    char[] val = t.toCharArray();

    int start = 0;
    int end = val.length;

    while ((start < end) && !Character.isLetterOrDigit(val[start])) {
      start++;
    }
    while ((start < end) && !Character.isLetterOrDigit(val[end - 1])) {
      end--;
    }
    return ((start > 0) || (end < val.length)) ? t.substring(start, end) : t;
  }


  private static class CategoryData {
    private int sampleCount;
    private SparseIntArray phraseCount = new SparseIntArray();
  }
  
  
  @Override
  public synchronized int classify (IDocumentContents docContents) {
    if (categories.size() == 0) {
      return -1;
    }
    
    boolean[] phraseUsed = new boolean[dictionary.size()];

    // Get the phrases used in this document
    for (ISegment seg : docContents.getSegments()) {
      if (seg.getType() == SegmentType.TEXT || seg.getType() == SegmentType.COMPANY_NUMBER) {
        String phrase = agressiveTrim(seg.getText());
        if (phrase.length() > 1) {
          int p = dictionary.get(phrase);
          if (p != -1) {
            phraseUsed[p] = true;
          }
        }
      }
    }

    // Count the significant words for each category (mostly used in one document, minimally used in all other documents)
    int [] matched = new int[categories.size()];
      
    for (int j = 0; j < phraseUsed.length; j++) {
      if (phraseUsed[j]) {
        int maxCount = 0;
        int minCount = 0;
        int maxCategory = -1;
       
        for (int i = 0; i < categories.size(); i++) {
          CategoryData category = categories.get(i);
          int phraseCount = category.phraseCount.get(j, 0);
          double aveCount = ((double)phraseCount) / category.sampleCount;
          if (aveCount > 0.9) {
            maxCount++;
            maxCategory = i;
          }
          if (aveCount < 0.1) {
            minCount++;
          }
        }
        if (maxCount == 1 && minCount == categories.size() - 1) {
          matched[maxCategory]++;
        }
      }
    }

    double maxMatched = 0;
    int matchedCategory = -1;
    for (int i = 0; i < matched.length; i++) {
      if (matched[i] > maxMatched) {
        maxMatched = matched[i];
        matchedCategory = i;
      }
    }
    return matchedCategory;
  }
    
  
  @Override
  public synchronized void train (IDocumentContents docContents, int category) {
    boolean[] phraseUsed = new boolean[dictionary.size()];

    // Update the dictionary with phrases used in this document
    for (ISegment seg : docContents.getSegments()) {
      if (seg.getType() == SegmentType.TEXT || seg.getType() == SegmentType.COMPANY_NUMBER) {
        String phrase = agressiveTrim(seg.getText());
        if (phrase.length() > 1) {
          int p = dictionary.resolve(phrase);
          if (p < phraseUsed.length) {
            phraseUsed[p] = true;
          }
        }
      }
    }

    CategoryData categoryData = categories.get(category);
    
    // Update the phrase count for each category
    int j = 0;
    while (j < phraseUsed.length) {
      if (phraseUsed[j]) {
        categoryData.phraseCount.increment(j);
      }
      j++;
    }
    while (j < dictionary.size()) {
      categoryData.phraseCount.increment(j);
      j++;
    }
    categoryData.sampleCount++;
  }
    

}
