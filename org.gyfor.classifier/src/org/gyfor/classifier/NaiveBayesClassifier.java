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

package org.gyfor.classifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gyfor.docstore.Document;
import org.gyfor.docstore.ISegment;
import org.gyfor.docstore.SegmentType;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;


public class NaiveBayesClassifier implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -123455813150452885L;

  private static final int MIN_WORD_LENGTH = 3;
  private static final int MAX_WORD_LENGTH = 40;
  
  private final WordDictionary dictionary = new WordDictionary();
  
  
  /** The training data gathered so far. */
  private Instances mData = null;
  
  private Instances trainingData = null;
  private ArrayList<Attribute> attributes1;

  /** The filter used to generate the word counts. */
  private StringToWordVector mFilter = new StringToWordVector();

  /** The actual classifier. */
  private Classifier mClassifier = new NaiveBayes();
  private Classifier mClassifier2 = new NaiveBayes();

  /** Whether the model is up to date. */
  private boolean mUpToDate;


  /**
   * Constructs empty training dataset.
   */
  public NaiveBayesClassifier(List<String> partyNames) {
    String nameOfDataset = "J48ClassificationData";

    // Create vector of attributes.
    ArrayList<Attribute> attributes = new ArrayList<Attribute>(2);

    // Add attribute for holding messages.
    attributes.add(new Attribute("__Message", (ArrayList<String>)null));

    // Add class attributes.
    attributes.add(new Attribute("__Class", partyNames));

    // Create dataset with initial capacity of 100, and set index of class.
    mData = new Instances(nameOfDataset, attributes, 100);
    
    System.out.println("attributes " + attributes.size());
    System.out.println("attributes " + mData.numAttributes());
    mData.setClassIndex(mData.numAttributes() - 1);

    attributes1 = new ArrayList<>();
    attributes1.add(new Attribute("___class", partyNames));
    trainingData = new Instances("DocumentParty", attributes1, 100);
    trainingData.setClassIndex(0);;
  }


  /**
   * Updates model using the given training message.
   *
   * @param message
   *          the message content
   * @param classValue
   *          the class label
   */
  public void trainClassifier(String message, Document doc, String classValue) {
    // Make message into instance.
    Instance instance = makeInstance(message, classValue, mData);

    // Add instance to training data.
    mData.add(instance);

    mUpToDate = false;
    
    Instance instance2 = makeInstance(doc, classValue, trainingData);
    trainingData.add(instance2);
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
  public double classifyMessage(String message, Document doc) {
    // Check whether classifier has been built.
    if (mData.numInstances() == 0) {
      return 0.0;
      //throw new RuntimeException("No classifier available.");
    }
    
    // Get index of predicted class value.
    double predicted;
    double predicted2;
    
    try {
      // Check whether classifier and filter are up to date.
      if (!mUpToDate) {
        // Initialize filter and tell it about the input format.
        mFilter.setInputFormat(mData);

        // Generate word counts from the training data.
        Instances filteredData = Filter.useFilter(mData, mFilter);
        
//        System.out.println(">>>> >>>>" + filteredData.numAttributes() + " " + filteredData.numClasses());
//        for (int i = 0; i < filteredData.size(); i++) {
//          Instance fd = filteredData.get(i);
//          for (Enumeration<Attribute> e = fd.enumerateAttributes(); e.hasMoreElements(); ) {
//            Attribute ex = e.nextElement();
//            System.out.println("         " + ex.name() + " " + ex.type() + "  " + Attribute.STRING);
//          }
//          System.out.println(">>>> >>>>" + fd);
//        }
        
        // Rebuild classifier.
        mClassifier.buildClassifier(filteredData);
        mClassifier2.buildClassifier(trainingData);

        mUpToDate = true;
      }

      // Make separate little test set so that message
      // does not get added to string attribute in mData.
      Instances testset = mData.stringFreeStructure();
      Instances testset2 = trainingData.stringFreeStructure();

      // Make message into test instance.
      Instance instance = makeInstance(message, null, testset);
      Instance instance2 = makeInstance(doc, null, testset2);

      // Filter instance.
      mFilter.input(instance);
      Instance filteredInstance = mFilter.output();

      predicted = mClassifier.classifyInstance(filteredInstance);
      predicted2 = mClassifier2.classifyInstance(instance2);
      
      // Output class value.
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return predicted2;
  }


  public String getClassValue (double prediction) {
    return mData.classAttribute().value((int)prediction);
  }
  
  
  /**
   * Method that converts a text message into an instance.
   *
   * @param text
   *          the message content to convert
   * @param data
   *          the header information
   * @return the generated Instance
   */
  private Instance makeInstance(String text, String classValue, Instances data) {
    // Create instance of length two.
    Instance instance = new DenseInstance(2);

    // Set value for message attribute
    Attribute messageAtt = data.attribute("__Message");
    instance.setValue(messageAtt, messageAtt.addStringValue(text));

    // Give instance access to attribute information from the dataset.
    instance.setDataset(data);

    if (classValue != null) {
      instance.setClassValue(classValue);
    }
    return instance;
  }
  
  
  private Instance makeInstance(Document doc, String classValue, Instances data) {
    int[] indexes = new int[0];
    double[] values = new double[0];
    for (ISegment seg : doc.getContents().getSegments()) {
      if (seg.getType() == SegmentType.TEXT) {
        String text = seg.getText();
        text = agressiveTrim(text);
        if (text.length() >= MIN_WORD_LENGTH && text.length() <= MAX_WORD_LENGTH) {
          int wordIndex;
          if (classValue != null) {
            // We are training, not classifying
            wordIndex = dictionary.getWordIndex(text);
          } else {
            wordIndex = dictionary.queryWordIndex(text);
          }
          if (wordIndex >= 0) {
            int x = Arrays.binarySearch(indexes, wordIndex);
            if (x >= 0) {
              values[x]++;
            } else {
              int x1 = -(x + 1);
              indexes = Arrays.copyOf(indexes, indexes.length + 1);
              System.arraycopy(indexes, x1, indexes, x1 + 1, indexes.length - (x1 + 1));
              indexes[x1] = wordIndex;
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

    Instance instance = new SparseInstance(1.0, values, indexes, dictionary.size());

    int i = data.numAttributes();
    while (i < dictionary.size()) {
      String word = dictionary.getWord(i);
      Attribute attrib = new Attribute(word);
      data.insertAttributeAt(attrib, i);
      i++;
    }
    
    // Give instance access to attribute information from the dataset.
    instance.setDataset(data);

    if (classValue != null) {
      // We are training, not classifying
      instance.setClassValue(classValue);
    }
    return instance;
  }

  
  public int getDictionarySize() {
    return dictionary.size();
  }
  
}
