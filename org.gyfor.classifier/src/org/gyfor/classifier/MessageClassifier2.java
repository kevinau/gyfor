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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;


/**
 * Java program for classifying short text messages into two classes 'miss' and
 * 'hit'.
 * <p/>
 * See also wiki article <a href=
 * "http://weka.wiki.sourceforge.net/MessageClassifier">MessageClassifier</a>.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 6054 $
 */
public class MessageClassifier2 {

  /** The training data gathered so far. */
  private Instances mData = null;

  /** The filter used to generate the word counts. */
  private StringToWordVector mFilter = new StringToWordVector();

  /** The actual classifier. */
  private Classifier mClassifier = new J48(); //NaiveBayesMultinomialUpdateable();

  /** Whether the model is up to date. */
  private boolean mUpToDate;


  /**
   * Constructs empty training dataset.
   */
  public MessageClassifier2() {
    String nameOfDataset = "MessageClassificationProblem";

    // Create vector of attributes.
    ArrayList<Attribute> attributes = new ArrayList<Attribute>(2);

    // Add attribute for holding messages.
    attributes.add(new Attribute("__Message", (ArrayList<String>) null));

    // Add class attribute.
    ArrayList<String> classValues = new ArrayList<String>(11);
    classValues.add("AFI");
    classValues.add("BHP");
    classValues.add("CBA");
    classValues.add("EGP");
    classValues.add("QAN");
    classValues.add("SGR");
    classValues.add("TAH");
    classValues.add("TCL");
    classValues.add("TLS");
    classValues.add("WES");
    classValues.add("WOW");
    attributes.add(new Attribute("__Class", classValues));

    // Create dataset with initial capacity of 100, and set index of class.
    mData = new Instances(nameOfDataset, attributes, 100);
    mData.setClassIndex(mData.numAttributes() - 1);
  }


  /**
   * Updates model using the given training message.
   *
   * @param message
   *          the message content
   * @param classValue
   *          the class label
   */
  public void updateData(String message, String classValue) {
    // Make message into instance.
    Instance instance = makeInstance(message, mData);

    // Set class value for instance.
    instance.setClassValue(classValue);

    // Add instance to training data.
    mData.add(instance);
    mUpToDate = false;
  }


  /**
   * Classifies a given message.
   *
   * @param message
   *          the message content
   * @throws Exception
   *           if classification fails
   */
  public double classifyMessage(String message) throws Exception {
    // Check whether classifier has been built.
    if (mData.numInstances() == 0) {
      throw new Exception("No classifier available.");
    }
    
    // Check whether classifier and filter are up to date.
    if (!mUpToDate) {
      // Initialize filter and tell it about the input format.
      mFilter.setInputFormat(mData);

      // Generate word counts from the training data.
      Instances filteredData = Filter.useFilter(mData, mFilter);

      // Rebuild classifier.
      mClassifier.buildClassifier(filteredData);

      mUpToDate = true;
    }

    // Make separate little test set so that message
    // does not get added to string attribute in mData.
    Instances testset = mData.stringFreeStructure();

    // Make message into test instance.
    Instance instance = makeInstance(message, testset);

    // Filter instance.
    mFilter.input(instance);
    Instance filteredInstance = mFilter.output();

    // Get index of predicted class value.
    double predicted = mClassifier.classifyInstance(filteredInstance);

    // Output class value.
    return predicted;
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
  private Instance makeInstance(String text, Instances data) {
    // Create instance of length two.
    Instance instance = new DenseInstance(2);

    // Set value for message attribute
    Attribute messageAtt = data.attribute("__Message");
    instance.setValue(messageAtt, messageAtt.addStringValue(text));

    // Give instance access to attribute information from the dataset.
    instance.setDataset(data);

    return instance;
  }


  /**
   * Main method. The following parameters are recognized:
   * <ul>
   * <li><code>-m messagefile</code><br/>
   * Points to the file containing the message to classify or use for updating
   * the model.</li>
   * <li><code>-c classlabel</code><br/>
   * The class label of the message if model is to be updated. Omit for
   * classification of a message.</li>
   * <li><code>-t modelfile</code><br/>
   * The file containing the model. If it doesn't exist, it will be created
   * automatically.</li>
   * </ul>
   *
   * @param args
   *          the commandline options
   */
  public static void main(String[] args) {
    try {
      // Read message file into string.
      BufferedReader br = new BufferedReader(new FileReader("C:/Users/Kevin/data/weka-model.txt"));
      MessageClassifier2 messageCl = new MessageClassifier2();
      Map<String, Integer> partyCounts = new HashMap<>();
      int triedCount = 0;
      int errorCount = 0;
      
      String message = br.readLine();
      while (message != null) {
        String classValue = "";
        int n = message.indexOf('|');
        if (n >= 0) {
          classValue = message.substring(n + 1);
          message = message.substring(0,  n);
        }
        //message = message.replace(" Class ", " ");
      
        Integer partyCount = partyCounts.get(classValue);
        if (partyCount == null) {
          partyCount = new Integer(0);
        }
        
        // Process message.
        if (messageCl.mData.numInstances() == 0) {
          System.err.println(classValue + " no classification data");
          System.err.flush();
        } else {
          double predicted = messageCl.classifyMessage(message.toString());
          String predictedx = messageCl.mData.classAttribute().value((int)predicted);
          if (predictedx.equals(classValue)) {
            System.out.println(classValue + " message classified as : " + messageCl.mData.classAttribute().value((int)predicted));
            System.out.flush();
          } else {
            System.err.println(classValue + " message wrongley classified as : " + messageCl.mData.classAttribute().value((int)predicted) + " after " + partyCount);
            System.err.flush();
            errorCount++;
          }
        }
        triedCount++;
        
        partyCount = partyCount + 1;
        partyCounts.put(classValue, partyCount);
        System.out.println("Update data: " + classValue);
        System.out.flush();
        messageCl.updateData(message.toString(), classValue);

        message = br.readLine();
      }
      br.close();
      System.out.println(errorCount + " errors out of " + triedCount + ", " + (errorCount * 100 / triedCount) + "%");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
