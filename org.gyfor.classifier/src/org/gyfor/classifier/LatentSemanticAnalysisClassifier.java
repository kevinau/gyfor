package org.gyfor.classifier;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.gyfor.classifier.impl.Dictionary;
import org.gyfor.classifier.impl.SparseIntArray;
import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.ISegment;
import org.gyfor.doc.SegmentType;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class LatentSemanticAnalysisClassifier implements IDocumentClassifier {

  private Dictionary dictionary = new Dictionary();
  
  private SparseIntArray gf = new SparseIntArray();
  
  private SparseIntArray df = new SparseIntArray();
  
  private List<SparseIntArray> tf = new ArrayList<>();
  
  private List<String> dc = new ArrayList<>();
  
  private ArrayList<Attribute> attributes = new ArrayList<>();

  private List<String> categorySet = new ArrayList<>();

  private int n = 0;
  
  
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

  
  private Instance buildInstanceFromFrequency (SparseIntArray termFreq, boolean[] duplicates, boolean targetInstance) {
    int tcn = termFreq.keyDataSize();
    
    double[] values = new double[tcn];
    int[] indexes = new int[tcn];
    
    int indexCount = 0;
    int i = 0;
    while (i < tcn) {
      if (!duplicates[i]) {
        int ti = termFreq.keyAt(i);
        indexes[i] = ti;
        int tfij = termFreq.valueAt(i);
        double lij = Math.log(tfij) + 1.0;
        // Inverse document frequency.  This is used (instead of entropy) because entropy looks 
        // more difficult to calculate
        double dfi = df.get(ti);
        double gi = Math.log1p(n / dfi);
        //if (dictionary.getWord(wi).startsWith("ASX Code")) {
        //  System.out.println(">> " + dfi + " " + (1 + (n / dfi)) + "  " + gi + " " + dictionary.getWord(wi));
        System.out.println(">> " + lij * gi + " " + dictionary.getWord(ti));
        //}
        values[i] = lij * gi;
        indexCount++;
      }
      i++;
    }
    System.out.println("====== " + targetInstance + " " + termFreq.size() + " -> " + indexCount);
    
    Instance instance = new SparseInstance(1.0, values, indexes, indexCount);
    return instance;
  }
  
  
  private Instance buildInstanceFromFrequency2 (SparseIntArray termFreq, boolean[] duplicates, int uniqueSize, boolean targetInstance) {
    int tcn = termFreq.keyDataSize();
    
    double[] values = new double[uniqueSize + 1];
    values[0] = Double.NaN;
    
    int i = 0;
    int j = 1;
    while (i < tcn) {
      if (!duplicates[i]) {
        int ti = termFreq.keyAt(i);
        int tfij = termFreq.valueAt(i);
        double lij = Math.log(tfij) + 1.0;
        // Inverse document frequency.  This is used (instead of entropy) because entropy looks 
        // more difficult to calculate
        double dfi = df.get(ti);
        double gi = Math.log1p(n / dfi);
        //if (dictionary.getWord(wi).startsWith("ASX Code")) {
        //  System.out.println(">> " + dfi + " " + (1 + (n / dfi)) + "  " + gi + " " + dictionary.getWord(wi));
        System.out.println(">> " + lij * gi + " " + dictionary.getWord(ti));
        //}
        values[j++] = lij * gi;
      }
      i++;
    }
    System.out.println("====== " + targetInstance + " " + termFreq.size() + " -> " + values.length + " " + uniqueSize);
    
    Instance instance = new DenseInstance(1.0, values);
    return instance;
  }
  
  
  private void buildMatrixA (SparseIntArray termFreq, RealMatrix matrixA, int j) {
    int tcn = termFreq.keyDataSize();
    
    for (int i = 0; i < tcn; i++) {
      int ti = termFreq.keyAt(i);
      int tfij = termFreq.valueAt(i);
      double lij = Math.log(tfij) + 1.0;
      // Inverse document frequency.  This is used (instead of entropy) because entropy looks 
      // more difficult to calculate
      double dfi = df.get(ti);
      double gi = Math.log1p(n / dfi);
      matrixA.setEntry(i, j, lij * gi);
    }
  }
  
  
  private void compareTerms (RealMatrix u, RealMatrix s, boolean[] duplicates) {
    for (int i = 0; i < dictionary.size(); i++) {
      if (!duplicates[i]) {
        RealVector iv = s.operate(u.getRowVector(i));
        if (iv.getL1Norm() == 0) {
          //System.out.println(">>>>>>>> " + iv + " >>> " + dictionary.getWord(i));
        } else {
          for (int p = i + 1; p < dictionary.size(); p++) {
            RealVector pv = s.operate(u.getRowVector(p));
            if (pv.getL1Norm() != 0) {
              double cos = iv.cosine(pv);
              if (cos >= 0.999999) {
                //System.out.print(dictionary.getWord(i) + "  " + dictionary.getWord(p) + ":  ");
                //System.out.println(cos);
                duplicates[p] = true;
              }
            }
          }
        }
      }
    }
  }
  
  
  @Override
  public String classify(IDocumentContents docContents) {
    System.out.println(">>>>>>>>> classifying docContents after " + n + " training documents, dictionary " + dictionary.size() + ", " + categorySet.size() + " categories");
    System.out.println();

    int categorySetSize = categorySet.size();
    if (categorySetSize == 0) {
      // No trained document--we cannot classify the document.
      return null;
    }
    if (categorySetSize == 1) {
      // All trained documents fit into one category.  Classify the new document as that.
      return categorySet.get(0);
    }
    
    RealMatrix matrixA = new Array2DRowRealMatrix (dictionary.size(), n);
    for (int j = 0; j < n; j++) {
      SparseIntArray termFreq = tf.get(j);
      buildMatrixA(termFreq, matrixA, j);
    }
    
    SingularValueDecomposition svd = new SingularValueDecomposition(matrixA);
    RealMatrix u = svd.getU();
    RealMatrix s = svd.getS();
    RealMatrix v = svd.getV();
    
    boolean[] duplicates = new boolean[dictionary.size()];
    compareTerms(u, s, duplicates);
    int uniqueCount = 0;
    for (int i = 0; i < dictionary.size(); i++) {
      if (!duplicates[i]) {
        System.out.println("... " + dictionary.getWord(i));
        uniqueCount++;
      }
    }
 
    attributes = new ArrayList<>();
    Attribute attrib = new Attribute("__class", categorySet);
    attributes.add(attrib);
    int i = 0;
    while (i < dictionary.size()) {
      if (!duplicates[i]) {
        String word = dictionary.getWord(i);
        attrib = new Attribute(word);
        attributes.add(attrib);
      }
      i++;
    }

    Instances trainingData = new Instances("DocumentParty", attributes, attributes.size());
    trainingData.setClassIndex(0);
    
    for (int j = 0; j < n; j++) {
      SparseIntArray termFreq = tf.get(j);
      Instance instance = buildInstanceFromFrequency2(termFreq, duplicates, uniqueCount, false);
//      RealVector termFreq2 = ak.getColumnVector(j);
//      Instance instance2 = buildInstanceFromFrequency(termFreq2);

      instance.setDataset(trainingData);
      instance.setClassValue(dc.get(j));
      // Give instance access to attribute information from the dataset.
      trainingData.add(instance);
    }
    Classifier nbClassifier = new NaiveBayes();
    try {
      nbClassifier.buildClassifier(trainingData);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    
    // Build instance for document that is being classified
    SparseIntArray docTermFreq = new SparseIntArray();
    
    List<? extends ISegment> segments = docContents.getSegments();
    for (ISegment segment : segments) {
      if (segment.getType() == SegmentType.TEXT || segment.getType() == SegmentType.COMPANY_NUMBER) {
        //System.out.println("............" + segment);
        String term = agressiveTrim(segment.getText());
        int wi = dictionary.query(term);
        if (wi != -1) {
          docTermFreq.increment(wi);
        }
      }
    }
    Instance instance = buildInstanceFromFrequency2(docTermFreq, duplicates, uniqueCount, true);
    instance.setDataset(trainingData);
    instance.setClassValue(Double.NaN);
    
    double predicted = 0;
    try {
      predicted = nbClassifier.classifyInstance(instance);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    System.out.println("Dictionary size: " + dictionary.size());
    return categorySet.get((int)predicted);
  }

  
  @Override
  public void train(IDocumentContents docContents, String category) {
    SparseIntArray docWordCounts = new SparseIntArray();
    
    List<? extends ISegment> segments = docContents.getSegments();
    for (ISegment segment : segments) {
      if (segment.getType() == SegmentType.TEXT || segment.getType() == SegmentType.COMPANY_NUMBER) {
        //System.out.println("............" + segment);
        String s = agressiveTrim(segment.getText());
        int wi = dictionary.resolve(s);
        docWordCounts.increment(wi);
        gf.increment(wi);
      }
    }
    tf.add(docWordCounts);
    dc.add(category);
    if (!categorySet.contains(category)) {
      categorySet.add(category);
    }
    n++;  
    
    for (int i = 0; i < docWordCounts.keyDataSize(); i++) {
      if (docWordCounts.valueAt(i) > 0) {
        int wi = docWordCounts.keyAt(i);
        df.increment(wi);
      }
    }
    
    // Dump df
//    for (int j = 0; j < n; j++) {
//      for (int wi = 0; wi < dictionary.size(); wi++) {
//        if (dictionary.getWord(wi).startsWith("ASX Code")) {
//          System.out.println(j + ".......... " + tf.get(j).get(wi) + "  " + gf.get(wi, 0) + "  " + df.get(wi, 0) + "  " + dictionary.getWord(wi));
//        }
//      }
//    }
  }

}
