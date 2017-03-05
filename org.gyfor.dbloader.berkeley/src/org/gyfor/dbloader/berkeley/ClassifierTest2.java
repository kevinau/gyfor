package org.gyfor.dbloader.berkeley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
import org.gyfor.docstore.parser.impl.PDFBoxPDFParser;
import org.gyfor.docstore.parser.impl.TesseractImageOCR;
import org.gyfor.docstore.parser.util.SparseIntArray;


public class ClassifierTest2 {

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
    List<String> categories = new ArrayList<>();
    List<Integer> categoryCounts = new ArrayList<>();
    List<SparseIntArray> allPhraseCounts = new ArrayList<>();
    SparseIntArray phraseCounts = new SparseIntArray();
    List<Integer> categorySamples = new ArrayList<>();

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
            // System.out.println(">>>>> " + phrase);
            int p = dictionary.resolve(phrase);
            while (p >= phraseUsed.size()) {
              phraseUsed.add(false);
            }
            phraseUsed.set(p, true);
          }
        }
      }

      // Get category and increment counts
      String fileName = file.getName();
      String category = fileName.substring(0, 3);
      if (!categories.contains(category)) {
        categories.add(category);
        categoryCounts.add(0);
        categorySamples.add(0);
        allPhraseCounts.add(new SparseIntArray());
      }
      int categoryIndex = categories.indexOf(category);

      int n2 = categorySamples.get(categoryIndex);
      categorySamples.set(categoryIndex, n2 + 1);

      SparseIntArray categoryPhraseCounts = allPhraseCounts.get(categoryIndex);
      for (int j = 0; j < phraseUsed.size(); j++) {
        if (phraseUsed.get(j)) {
          int nn = categoryPhraseCounts.get(j);
          categoryPhraseCounts.put(j, nn + 1);
          nn = phraseCounts.get(j);
          phraseCounts.put(j, nn + 1);
          nn = categoryCounts.get(categoryIndex);
          categoryCounts.set(categoryIndex, nn + 1);
        }
      }

      // for (int i = 0; i < categories.size(); i++) {
      // categoryPhraseCounts = allPhraseCounts.get(i);
      // for (int j = 0; j < phraseUsed.size(); j++) {
      // int count = categoryPhraseCounts.get(j);
      // System.out.println(j + ": " + count + " " + dictionary.getWord(j));
      // }
      // }

      m++;
      if (m > 3) {
        break;
      }

    }

    // Dump all counts
    try (PrintWriter out = new PrintWriter("xxx.csv")) {
      out.print("i,Phrase");
      for (int i = 0; i < categories.size(); i++) {
        out.print("," + categories.get(i));
      }
      out.println(",totals");

      for (int j = 0; j < dictionary.size(); j++) {
        out.print(j + ",\"" + dictionary.getWord(j).replace('"', ' ') + "\"");

        double totalCount = 0;
        for (int i = 0; i < categories.size(); i++) {
          SparseIntArray categoryPhraseCounts = allPhraseCounts.get(i);
          double scaledCount = categoryPhraseCounts.get(j) / ((double)categorySamples.get(i));
          // out.print("," + scaledCount);
          totalCount += scaledCount;
        }

        double[] aveCount = new double[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
          SparseIntArray categoryPhraseCounts = allPhraseCounts.get(i);
          double scaledCount = categoryPhraseCounts.get(j) / ((double)categorySamples.get(i));
          aveCount[i] = scaledCount / totalCount;
          out.print("," + aveCount[i]);
        }
        out.println();
      }

      out.println();
      out.print(",");
      for (int i = 0; i < categories.size(); i++) {
        out.print("," + categoryCounts.get(i));
      }
      out.println();
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static class RelevantPhrase implements Comparable<RelevantPhrase> {

    private int count;
    private String text;


    RelevantPhrase(int count, String text) {
      this.count = count;
      this.text = text;
    }


    @Override
    public int compareTo(RelevantPhrase other) {
      int n = other.count - this.count;
      if (n == 0) {
        return this.text.compareTo(other.text);
      } else {
        return n;
      }
    }

  }


  private static void reportRelevantPhrases(String category, int totalSeen, int priorSeen, Dictionary dictionary,
      SparseIntArray categoryPhraseCounts) {
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
