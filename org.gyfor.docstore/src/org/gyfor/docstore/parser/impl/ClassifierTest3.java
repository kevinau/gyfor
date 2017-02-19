package org.gyfor.docstore.parser.impl;

import java.io.File;
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
import org.gyfor.docstore.parser.util.SparseIntArray;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;


public class ClassifierTest3 {

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

    int matched0 = 0;
    int matched1 = 0;
    int totalSamples = 0;
    
    int m = 0;
    for (File file : files) {
      Path path = file.toPath();
      System.out.println();
      System.out.println(path);

      String id = path.getFileName().toString();
      // Remove PDF extension
      id = id.substring(0, id.length() - 4);

      List<Boolean> phraseUsed = new ArrayList<>();

      int priorWordCount = dictionary.size();
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

      // Build an instances object and populate it with prior seen data
      if (categories.size() > 1) {
        Instances priorDataSet = buildDataset("Prior", categories, dictionary, priorWordCount);
        loadInstances (priorDataSet, categories, dictionary, priorWordCount);
        //System.out.println(priorDataSet);
        
        // Build and train classifier
        try {
          Classifier classifier = new NaiveBayes();
          classifier.buildClassifier(priorDataSet);
          
          // Make separate little test set so that message
          // does not get added to string attribute in mData.
          Instances testDataSet = priorDataSet.stringFreeStructure();
          loadTestInstance (testDataSet, phraseUsed, dictionary, priorWordCount);
          Instance testInstance = testDataSet.firstInstance();
          //System.out.println(testInstance);

          double[] dist = classifier.distributionForInstance(testInstance);
          double maxd = 0;
          int result = 0;
          int i = 0;
          for (double d : dist) {
            if (d > maxd) {
              maxd = d;
              result = i;
            }
            i++;
          }
          String testName = file.getName().substring(0, 3);
          int expected = -1;
          for (CategoryData c : categories) {
            if (c.name.equals(testName)) {
              expected = c.index;
              break;
            }
          }
          if (expected == -1) {
            System.out.println("First seen " + file.getName());
            matched0++;
          } else {
            if (expected == result) {
              System.out.println("Success on " + file.getName());
              matched1++;
            } else {
              System.out.println(priorDataSet);
              System.out.println(testInstance);
              i = 0;
              for (double d : dist) {
                System.out.println(i + "  " + d);
                i++;
              }
              System.out.println("Failure on " + file.getName() + ", classified as " + categories.get(result).name);
            }
          }

          //System.out.println(">>>>>>>> result = " + result + "  " + categories.get((int)result).name + " vs " + file.getName());
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      } else {
        matched0++;
      }
      totalSamples++;
      
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
      }
      category.sampleCount++;
 
      for (int j = 0; j < phraseUsed.size(); j++) {
        if (phraseUsed.get(j)) {
          category.phraseCount.increment(j);
        }
      }

      m++;
      //System.out.println(">>>>>>>>>>>>> " + m);
      if (m > 1000) {
        break;
      }

    }
    
    System.out.println("Matched: " + matched1 + " out of " + (totalSamples - matched0) + " = " + (matched1 * 100.0 / (totalSamples - matched0)) + "%");
   

//    // Dump all counts
//    try (PrintWriter out = new PrintWriter("xxx3.csv")) {
//      out.print("i,Phrase");
//      for (int i = 0; i < categories.size(); i++) {
//        out.print("," + categories.get(i).name);
//      }
//      out.println(",totals");
//
//      for (int j = 0; j < dictionary.size(); j++) {
//        out.print(j + ",\"" + dictionary.getWord(j).replace('"', ' ') + "\"");
//
//        double totalPhraseUsage = 0;
//        for (int i = 0; i < categories.size(); i++) {
//          CategoryData category = categories.get(i);
//          double phraseUsage = category.phraseCount.get(j);
//          phraseUsage /= category.sampleCount;
//          totalPhraseUsage += phraseUsage;
//        }
//        for (int i = 0; i < categories.size(); i++) {
//          CategoryData category = categories.get(i);
//          double phraseUsage = category.phraseCount.get(j);
//          phraseUsage /= category.sampleCount;
//          phraseUsage /= totalPhraseUsage;
//          out.print("," + phraseUsage);
//        }
//        out.println();
//      }
//
//      out.println();
//    } catch (FileNotFoundException ex) {
//      throw new RuntimeException(ex);
//    }
  }
  
  
  private static Instances buildDataset (String name, List<CategoryData> categories, Dictionary dictionary, int phraseCount) {
    ArrayList<Attribute> attributes = new ArrayList<Attribute>();

    for (int j = 0; j < phraseCount; j++) {
      Attribute attrib = new Attribute("phrase" + j);
      attributes.add(attrib);
    }

    ArrayList<String> classNames = new ArrayList<>();
    for (CategoryData c : categories) {
      classNames.add(c.name);
    }
    Attribute classAttrib = new Attribute("class", classNames);
    attributes.add(classAttrib);
    
    Instances dataSet = new Instances(name, attributes, 0);
    dataSet.setClassIndex(phraseCount);
    return dataSet;
  }

  
  private static void loadInstances (Instances instances, List<CategoryData> categories, Dictionary dictionary, int phraseCount) {
    double[][] data = new double[categories.size()][phraseCount + 1];
    
    for (int j = 0; j < phraseCount; j++) {
      double totalPhraseUsage = 0;
      for (int i = 0; i < categories.size(); i++) {
        CategoryData category = categories.get(i);
        double phraseUsage = category.phraseCount.get(j);
        phraseUsage /= category.sampleCount;
        totalPhraseUsage += phraseUsage;
      }
      for (int i = 0; i < categories.size(); i++) {
        CategoryData category = categories.get(i);
        double phraseUsage = category.phraseCount.get(j);
        phraseUsage /= category.sampleCount;
        phraseUsage /= totalPhraseUsage;
        data[i][j] = phraseUsage;
      }
    }
    
    // Set category (class) values
    for (int i = 0; i < categories.size(); i++) {
      data[i][phraseCount] = i;
    }

    // Load data in category order
    for (int i = 0; i < categories.size(); i++) {
      Instance instance = new DenseInstance(1.0, data[i]);
      instance.setDataset(instances );
      instances.add(instance);
    }
  }
  
  private static void loadTestInstance (Instances instances, List<Boolean> phraseUsed, Dictionary dictionary, int phraseCount) {
    double[] data = new double[phraseCount + 1];
    
    for (int j = 0; j < phraseCount; j++) {
      if (phraseUsed.get(j)) {
        data[j] = 1.0;
      }
    }
        
    // Set category (class) values
    data[phraseCount] = Double.NaN;

    Instance instance = new DenseInstance(1.0, data);
    instances.add(instance);
  }
  
}
