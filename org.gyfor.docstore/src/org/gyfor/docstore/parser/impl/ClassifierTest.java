package org.gyfor.docstore.parser.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gyfor.docstore.Dictionary;
import org.gyfor.docstore.DocumentStore;
import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.ISegment;
import org.gyfor.docstore.SegmentType;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.gyfor.docstore.parser.util.SparseIntArray;

public class ClassifierTest {

  private static String agressiveTrim(String t) {
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

  
  public static void main (String[] args) {
    File baseDir = new File(System.getProperty("user.home"), "/Scanned Stuff 2");
    String[] fileNames = baseDir.list();

    File[] files = new File[fileNames.length];
    int k = 0;
    for (String fileName : fileNames) {
       files[k] = new File(baseDir, fileName);
       k++;
    }
    Arrays.sort(files, new Comparator<File>() {
      @Override
      public int compare(File arg0, File arg1) {
        long n = arg0.lastModified() - arg1.lastModified();
        if (n == 0) {
          return arg0.getName().compareTo(arg1.getName());
        } else {
          return Long.signum(n);
        }
      }
    });
    
    IDocumentStore docStore = new DocumentStore();
    IImageParser imageParser = new TesseractImageOCR();
    IPDFParser pdfParser = new PDFBoxPDFParser(imageParser);
    Dictionary dictionary = new Dictionary();
    List<String> categories = new ArrayList<>();
    List<Integer> categoryCount = new ArrayList<>();
    List<SparseIntArray> allPhraseCounts = new ArrayList<>();
    SparseIntArray totalPhraseCounts = new SparseIntArray();
    
    int totalTrials = 0;
    int totalFails = 0;
    
    int m = 0;
    for (File file : files) {
      Path path = file.toPath();
      System.out.println();
      System.out.println(path);
      
      String id = path.getFileName().toString();
      // Remove PDF extension
      id = id.substring(0, id.length() - 4);
      
      List<Boolean> phraseUsed = new ArrayList<>();
      
      // Get the phrases used in this document
      IDocumentContents docContents = pdfParser.parseText(id, path, 600, docStore);
      for (ISegment seg : docContents.getSegments()) {
        if (seg.getType() == SegmentType.TEXT || seg.getType() == SegmentType.COMPANY_NUMBER) {
          String phrase = agressiveTrim(seg.getText());
          if (phrase.length() > 1) {
            //System.out.println(">>>>> " + phrase);
            int p = dictionary.resolve(phrase);
            while (p >= phraseUsed.size()) {
              phraseUsed.add(false);
            }
            phraseUsed.set(p, true);
          }
        }
      }
      
      // Compare the phrases used in this document with previously seen categories of documents
      double[] varSquared = new double[categories.size()];
      
      String target = file.getName().substring(0, 3);

      for (int i = 0; i < categories.size(); i++) {
        SparseIntArray categoryPhraseCounts = allPhraseCounts.get(i);
        double cc = categoryCount.get(i);
        
        reportRelevantPhrases (categories.get(i), totalPhraseCounts.get(i), categoryCount.get(i), dictionary, categoryPhraseCounts);

        // For each phrase that has been used in the target document, what is its use within the candidate 
        // documents?  Take the count for the candidate document, and divide by the count of this phrase in all prior 
        // documents.  This will give an average phrase count: 1.0 if this phrase occurs only in prior documents that
        // are in the same category of the target document.,
        // 1/n (where n is the number of categories) if the phrase occurs in all prior documents.
        // TODO This could be improved by calculating a SD and using that
        int usedCount = 0;
        for (int j = 0; j < phraseUsed.size(); j++) {
//          System.out.println(target + "," + categories.get(i) + ",\"" + dictionary.getWord(j).replace('"', ' ') + "\"," + phraseUsed.get(j) + "," + categoryPhraseCounts.get(j) + "," + totalPhraseCounts.get(j));

          if (phraseUsed.get(j)) {
            usedCount++;
            double totalPhraseCount = totalPhraseCounts.get(j);
            double categoryPhraseCount = categoryPhraseCounts.get(j);
            double cover = (categoryPhraseCount + 1) / (totalPhraseCount + 1);
            //double thisOffset;
            //thisOffset = 1.0;
            //double offset = (thisOffset - priorOffset);
            if (dictionary.getWord(j).contains("ASX Code") || dictionary.getWord(j).contains("xxxKINGDOM")) {
//              System.out.println(dictionary.getWord(j));
//              System.out.println("Total count: " + totalPhraseCount);
//              System.out.println("Category counts: " + categoryPhraseCount);
//              System.out.println(target + "...." + categories.get(i) + "  " + dictionary.getWord(j) + ": " + cover);
            }
            if (cover == 1.0) {
              varSquared[i] += 1.0;
            }
//            varSquared[i] += cover * cover * cover * cover;
//          System.out.println(categories.get(i) + " " + j + ": " + (phraseUsed.get(j) ? "u" : "-") + "  " + offset + "   " + dictionary.getWord(j));
          }
        }
        System.out.println(varSquared[i] + "   " + usedCount);
//        varSquared[i] /= usedCount;
        System.out.println("..........." + categories.get(i) + " " + varSquared[i]);
      }
      
      double maxVarSquared = -1;
      int maxCategory = -1;
      boolean found = false;
      for (int i = 0; i < categories.size(); i++) {
        if (varSquared[i] > maxVarSquared) {
          maxCategory = i;
          maxVarSquared = varSquared[i];
          found = true;
        }
      }

      if (found) {
        String fileName = file.getName();
        String category = fileName.substring(0, 3);
        int c = categories.indexOf(category);
        String cx;
        if (c == -1) {
          cx = "<new>";
        } else {
          cx = categories.get(c);
        }
        totalTrials++;
        if (maxCategory == c) {
          System.out.println(fileName + ": " + cx + ", found " + categories.get(maxCategory));
        } else {
          System.err.println(fileName + ": " + cx + ", found " + categories.get(maxCategory));
          totalFails++;
        }
      }
      
      // Get category and increment counts
      String fileName = file.getName();
      String category = fileName.substring(0, 3);
      if (!categories.contains(category)) {
        categories.add(category);
        categoryCount.add(0);
        allPhraseCounts.add(new SparseIntArray());
      }
      int categoryIndex = categories.indexOf(category);
      int n = categoryCount.get(categoryIndex);
      categoryCount.set(categoryIndex, n + 1);
      
      SparseIntArray categoryPhraseCounts = allPhraseCounts.get(categoryIndex);
      for (int j = 0; j < phraseUsed.size(); j++) {
        if (phraseUsed.get(j)) {
          int nn = categoryPhraseCounts.get(j);
          categoryPhraseCounts.put(j,  nn + 1);
          nn = totalPhraseCounts.get(j);
          totalPhraseCounts.put(j, nn + 1);
        }
      }

//      for (int i = 0; i < categories.size(); i++) {
//        categoryPhraseCounts = allPhraseCounts.get(i);
//        for (int j = 0; j < phraseUsed.size(); j++) {
//          int count = categoryPhraseCounts.get(j);
//          System.out.println(j + ": " + count + "   " + dictionary.getWord(j));
//        }
//      }

      m++;
      if (m > 500) {
        break;
      }
      
    }
    System.out.println();
    System.out.println("Total " + totalFails + " failures (" + categories.size() + " new) out of " + totalTrials + " trials");
  }


  private static class RelevantPhrase implements Comparable<RelevantPhrase> {
    private int count;
    private String text;
    
    RelevantPhrase (int count, String text) {
      this.count = count;
      this.text = text;
    }

    @Override
    public int compareTo(RelevantPhrase other) {
      int n = other.count - this.count;
      if (n == 0) {
        return  this.text.compareTo(other.text);
      } else {
        return n;
      }
    }
    
  }
  
  
  private static void reportRelevantPhrases (String category, int totalSeen, int priorSeen, Dictionary dictionary, SparseIntArray categoryPhraseCounts) {
    List<RelevantPhrase> rps = new ArrayList<>();
    
    // The following phrases are relevant to this category, and sort
    for (int j = 0; j < categoryPhraseCounts.size(); j++) {
      int count = categoryPhraseCounts.get(j);
      if (count > 0) {
        String text = dictionary.getWord(j);
        RelevantPhrase rp = new RelevantPhrase(count, text);
        rps.add(rp);
      }
    }
    Collections.sort(rps);
    
    for (RelevantPhrase rp : rps) {
      System.out.println("  " + category + ": " + totalSeen + " " + priorSeen + " " + rp.count + "  " + rp.text);
    }
  }

}
