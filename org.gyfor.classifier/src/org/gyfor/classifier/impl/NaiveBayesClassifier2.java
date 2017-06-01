/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    MessageClassifier.java
 *    Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package org.gyfor.classifier.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.ISegment;
import org.gyfor.doc.SegmentType;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;


public class NaiveBayesClassifier2 implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -123455813150452885L;

  private static final int MIN_WORD_LENGTH = 1;
  
  private final WordDictionary dictionary = new WordDictionary();
  
  private final List<String> classValues = new ArrayList<>();
  
  /** The training data gathered so far. */
  private Instances trainingData = null;
  private ArrayList<Attribute> attributes;

  /** The actual classifier. */
  private Classifier mClassifier;

  /** Whether the model is up to date. */
  private boolean mUpToDate;


  /**
   * Constructs empty training dataset.
   */
  public NaiveBayesClassifier2(List<String> classValues) {
    mClassifier = new NaiveBayes();
    attributes = new ArrayList<>();
    attributes.add(new Attribute("___class", new ArrayList<String>()));
    trainingData = new Instances("DocumentParty", attributes, 100);
    trainingData.setClassIndex(0);
    mUpToDate = false;
    
//    this.classValues.clear();
//    this.classValues.addAll(classValues);
  }


  private void ensureClass (String classValue) {
    if (classValues.contains(classValue)) {
      return;
    }
    classValues.add(classValue);
    
    mClassifier = new NaiveBayes();
    ArrayList<Attribute> attributes2 = new ArrayList<>();
    attributes2.add(new Attribute("___class", classValues));
    for (int i = 1; i < trainingData.numAttributes(); i++) {
      Attribute a = trainingData.attribute(i);
      attributes2.add(new Attribute(a.name()));
    }
    Instances trainingData2 = new Instances("DocumentParty", attributes2, trainingData.numInstances());
    trainingData2.setClassIndex(0);
    
    for (int i = 0; i < trainingData.numInstances(); i++) {
      Instance instance = trainingData.get(i);
      trainingData2.add(instance);
    }
    attributes = attributes2;
    trainingData = trainingData2;
    mUpToDate = false;
  }

  
  /**
   * Updates model using the given training message.
   *
   * @param message
   *          the message content
   * @param classValue
   *          the class label
   */
  public void trainClassifier(IDocumentContents docContents, String classValue) {
    ensureClass(classValue);
    Instance instance2 = makeInstance(docContents, classValue, trainingData);
    trainingData.add(instance2);
    mUpToDate = false;
  }
  
  
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

  
  /**
   * Classifies a given message.
   *
   * @param message
   *          the message content
   * @throws Exception
   *           if classification fails
   */
  public double classifyMessage(IDocumentContents docContents) {
    // Get index of predicted class value.
    double predicted;
    
    try {
      // Check whether the classifier is up to date.
      if (!mUpToDate) {
        // Rebuild classifier.
        mClassifier.buildClassifier(trainingData);
        mUpToDate = true;
      }

      // Make separate little test set so that message
      // does not get added to string attribute in mData.
      Instances testset2 = trainingData.stringFreeStructure();

      // Make message into test instance.
      Instance instance2 = makeInstance(docContents, null, testset2);

      predicted = mClassifier.classifyInstance(instance2);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return predicted;
  }


  public boolean isEmpty () {
    return trainingData.numInstances() == 0;
  }

  
  public int numClasses () {
    return classValues.size();
  }
  
  
  public String getClassValue (double prediction) {
    return trainingData.classAttribute().value((int)prediction);
  }
  
  
  private Instance makeInstance(IDocumentContents docContents, String classValue, Instances data) {
    int[] indexes = new int[1];
    double[] values = new double[1];
    if (classValue != null) {
      values[0] = trainingData.classAttribute().indexOfValue(classValue);
    } else {
      values[0] = Double.NaN;
    }
    for (ISegment seg : docContents.getSegments()) {
      if (seg.getType() == SegmentType.TEXT || seg.getType() == SegmentType.COMPANY_NUMBER) {
        String text = seg.getText();
        text = agressiveTrim(text);
        if (text.length() >= MIN_WORD_LENGTH) {
          int wordIndex;
          if (classValue != null) {
            // We are training, not classifying
            wordIndex = dictionary.getWordIndex(text);
          } else {
            wordIndex = dictionary.queryWordIndex(text);
          }
          if (wordIndex >= 0) {
            // Allow for the instance class
            int featureIndex = wordIndex + 1;
            int x = Arrays.binarySearch(indexes, featureIndex);
            if (x >= 0) {
              values[x]++;
            } else {
              int x1 = -(x + 1);
              indexes = Arrays.copyOf(indexes, indexes.length + 1);
              System.arraycopy(indexes, x1, indexes, x1 + 1, indexes.length - (x1 + 1));
              indexes[x1] = featureIndex;
              values = Arrays.copyOf(values, values.length + 1);
              System.arraycopy(values, x1, values, x1 + 1, values.length - (x1 + 1));
              values[x1] = 1.0;
            }
          }
        }
      }
    }
//  System.out.print(doc.getOriginName() + " " + doc.getId());
//  for (int j = 0; j < indexes.length; j++) {
//    int w = indexes[j];
//    System.out.println("," + w + " " + dictionary.getWord(w) + " " + values[j]);
//  }
//  System.out.println();

    Instance instance = new SparseInstance(1.0, values, indexes, dictionary.size() + 1);

    int i = data.numAttributes();
    while (i < dictionary.size() + 1) {
      String word = dictionary.getWord(i - 1);
      Attribute attrib = new Attribute(word);
      data.insertAttributeAt(attrib, i);
      i++;
    }
    
    // Give instance access to attribute information from the dataset.
    instance.setDataset(data);

    if (classValue != null) {
      // We are training, not classifying
/////////////////      instance.setClassValue(classValue);
    }
    return instance;
  }

  
  public int getDictionarySize() {
    return dictionary.size();
  }
  
}
