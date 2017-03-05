package org.gyfor.dbloader.berkeley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.gyfor.docstore.parser.impl.PDFBoxPDFParser;
import org.gyfor.docstore.parser.impl.TesseractImageOCR;
import org.gyfor.docstore.parser.util.SparseIntArray;


public class ClassifierTest4 {

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


  private static class CategoryData {
    private int index;
    private String name;
    private int sampleCount;
    private SparseIntArray phraseCount = new SparseIntArray();
  
    private CategoryData (int index, String name) {
      this.index = index;
      this.name = name;
    }
  }
  
  public static void main(String[] args) {
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
    List<CategoryData> categories = new ArrayList<>();

    int totalSamples = 0;
    int matched1 = 0;
    
    int m = 0;
    for (File file : files) {
      Path path = file.toPath();
      System.out.println();
      System.out.println(path);

      String id = path.getFileName().toString();
      // Remove PDF extension
      id = id.substring(0, id.length() - 4);

      boolean[] phraseUsed = new boolean[0];

      // Get the phrases used in this document
      IDocumentContents docContents = pdfParser.parseText(id, path, 600, docStore);
      for (ISegment seg : docContents.getSegments()) {
        if (seg.getType() == SegmentType.TEXT || seg.getType() == SegmentType.COMPANY_NUMBER) {
          String phrase = agressiveTrim(seg.getText());
          if (phrase.length() > 1) {
            int p = dictionary.resolve(phrase);
            if (p >= phraseUsed.length) {
              phraseUsed = Arrays.copyOf(phraseUsed, p + 1);
            }
            phraseUsed[p] = true;
          }
        }
      }

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

      // Get category and increment counts
      String fileName = file.getName();
      String categoryName = fileName.substring(0, 3);
      CategoryData category = null;
      for (CategoryData c : categories) {
        if (c.name.equals(categoryName)) {
          category = c;
          break;
        }
      }
      if (category == null) {
        category = new CategoryData(categories.size(), categoryName);
        categories.add(category);
      } else {
        totalSamples++;
      }
      category.sampleCount++;
 
      double maxMatched = 0;
      int matchedCategory = 0;
      int resultCount = 0;
      for (int i = 0; i < matched.length; i++) {
        if (matched[i] > maxMatched) {
          maxMatched = matched[i];
          matchedCategory = i;
          resultCount = 1;
        } else if (matched[i] == maxMatched) {
          // System.out.println(categories.get(i).name + ": equal match with: " + categories.get(matchedCategory).name);
          matchedCategory = i;
          resultCount = 2;
        }
      }
      System.out.println("Result: " + resultCount + "  " + categories.get(matchedCategory).name + " vs " + category.name);
      switch (resultCount) {
      case 0 :
        break;
      case 1 :
        if (matchedCategory == category.index) {
          matched1++;
        }
        break;
      case 2 :
        break;
      }
      
      for (int j = 0; j < phraseUsed.length; j++) {
        if (phraseUsed[j]) {
          category.phraseCount.increment(j);
        }
      }

      m++;
      //System.out.println(">>>>>>>>>>>>> " + m);
      if (m > 1000) {
        break;
      }

    }
    
    System.out.println("Matched: " + matched1 + " out of " + totalSamples + " = " + (matched1 * 100.0 / totalSamples) + "%");
 
    // Dump all counts
    try (PrintWriter out = new PrintWriter("xxx4.csv")) {
      out.print("i,Phrase");
      for (int i = 0; i < categories.size(); i++) {
        out.print("," + categories.get(i).name);
      }
      out.println(",totals");

      double[] values = new double[categories.size()];
      
      for (int j = 0; j < dictionary.size(); j++) {
        out.print(j + ",\"" + dictionary.getWord(j).replace('"', ' ') + "\"");

        for (int i = 0; i < categories.size(); i++) {
          CategoryData category = categories.get(i);
          values[i] = ((double)category.phraseCount.get(j)) / category.sampleCount;
        }

        for (int i = 0; i < categories.size(); i++) {
          CategoryData category = categories.get(i);
          int phraseCount = category.phraseCount.get(j);
          double aveCount = ((double)phraseCount) / category.sampleCount;
//          scaledCount = scaledCount * 2.0 - 1.0;
//          if (scaledCount < 0) {
//            scaledCount = -Math.sqrt(-scaledCount);
//          } else {
//            scaledCount = Math.sqrt(scaledCount);
//          }
//          scaledCount = (scaledCount + 1.0) / 2;
          out.print(", " + phraseCount + ",(" + aveCount + ")");
        }
        out.println();
      }

//      Arrays.sort(stdDevs);
//      int j1 = 0;
//      for (int j = 0; j < stdDevs.length; j++) {
//        System.out.println(j1 + ": " + stdDevs[j].stdDev + "  " + dictionary.getWord(stdDevs[j].phraseIndex));
//        j1++;
//      }
//      out.println();
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }
    
}
