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

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression;
import org.apache.mahout.classifier.sgd.CrossFoldLearner;
import org.apache.mahout.classifier.sgd.L1;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.gyfor.docstore.Document;
import org.gyfor.docstore.IDocumentStore;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;


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
//@Component(immediate=true)
public class DocumentPartyClassifier2  {

  private IDocumentStore docStore;
  
  private WordDictionary dictionary = new WordDictionary();
  private List<String> categories = new ArrayList<>();
  
  
  @Reference 
  public void setDocumentStore (IDocumentStore docStore) {
    this.docStore = docStore;
  }

  
  public void unsetDocumentStore (IDocumentStore docStore) {
    this.docStore = null;
  }

  
  /**
   * Constructs empty training dataset.
   */
  @Activate
  public void activate(ComponentContext componentComtext) {
    // Get all known documents
    List<Observation> observations = getObservations();

    int n = observations.size();
    
    int sampleCount = 0;
    int errorCount = 0;
    List<Integer> seen = new ArrayList<>();
    
    for (int i = 0; i < n - 1; i++) {
      Observation testData = observations.get(i);

      List<Observation> trainingData = observations.subList(0,  i);
      System.out.flush();
      System.err.flush();
      System.out.println("Training data: " + trainingData.size() + " observations.  One testing observation");

      sampleCount++;
      if (trainingData.isEmpty()) {
        System.out.println("== no prior data.  No prediction possible");
        seen.add(testData.getActual());
        continue;
      }

      // Train a model
      //////OnlineLogisticRegression olr = train(trainingData);
      new NaiveBayesModel()
      new StandardNaiveBayesClassifier(nbModel);
      AdaptiveLogisticRegression reg = new AdaptiveLogisticRegression(seen.size(), dictionary.size() + 1, new L1());
      new J48Classifier(partyNames)
      int totalTraining = 0;
      while (totalTraining < 30) {
        for (Observation td : trainingData) {
          ////System.out.println("Training data: " + td.getActual() + " > " + td.getVector());
          reg.train(td.getActual(), td.getVector());
          totalTraining++;
        }
      }
      reg.close();
      CrossFoldLearner bestLearner = reg.getBest().getPayload().getLearner();

      // Test the model
      DenseVector p = new DenseVector(seen.size());
      bestLearner.classifyFull(p, testData.getVector());
      for (int j = 0; j < seen.size(); j++) {
        System.out.println("== " + j + ": " + categories.get(j) + " " + p.get(j));
      }
      int result = p.maxValueIndex();
      System.out.flush();
      System.err.flush();
      if (result == testData.getActual()) {
        System.out.println("== OK " + result);
      } else {
        if (seen.contains(testData.getActual())) {
          errorCount++;
          System.err.println("== Got " + result + " when expecting " + testData.getActual() + ", " + testData.getName());
        } else {
          seen.add(testData.getActual());
          System.err.println("== First time " + testData.getActual() + " is seen");
        }
      }
      System.out.flush();
      System.err.flush();

      
      // Test the model
      //////////testModel(olr, testData);
    }
    if (sampleCount > 0) {
      System.out.println(errorCount + " errors out of " + sampleCount + " smaples (" + (errorCount * 100 / sampleCount) + "%)");
    }
  }

  
  private List<Observation> getObservations () {
    // Get all known documents
    List<Document> docs = new ArrayList<>();
    List<Observation> observations = new ArrayList<>();
    dictionary.clear();
    categories.clear();
    
    PrimaryIndex<String, Document> primaryIndex = docStore.getPrimaryIndex();
    try (EntityCursor<Document> cursor = primaryIndex.entities()) {
      for (Document doc : cursor) {
        // For training, get the partyName (for the moment)
        String partyName = doc.getOriginName().substring(0, 3);
        if (!categories.contains(partyName)) {
          categories.add(partyName);
          System.out.println(partyName);
        }
        int partyIndex = categories.indexOf(partyName);
        
        Observation observation = new Observation(doc, partyIndex, dictionary);
        observations.add(observation);
        
        docs.add(doc);
      }
    }
    System.out.println("Found " + docs.size() + " documents");
    System.out.println("Found " + dictionary.size() + " distinct words");
    return observations;
  }

  
//  private OnlineLogisticRegression train(List<Observation> trainData) {
//    OnlineLogisticRegression olr = new OnlineLogisticRegression(categories.size(), 100, new L1());
//    // Train the model using 30 passes
//    for (int pass = 0; pass < 30; pass++) {
//      for (Observation observation : trainData) {
//        olr.train(observation.getActual(), observation.getVector());
//      }
//      // Every 10 passes check the accuracy of the trained model
//      if (pass % 10 == 0) {
//        Auc eval = new Auc(0.5);
//        for (Observation observation : trainData) {
//          
//          eval.add(observation.getActual(), olr.classify(observation.getVector()));
//        }
//        System.out.format(
//            "Pass: %2d, Learning rate: %2.4f, Accuracy: %2.4f\n",
//            pass, olr.currentLearningRate(), eval.auc());
//      }
//    }
//    return olr;
//  }

  
  private void testModel (OnlineLogisticRegression olr, Observation testData) {
    Vector result = olr.classifyFull(testData.getVector());

    System.out.println("------------- Testing -------------");
    System.out.format("Probability of not fraud (0) = %.3f\n", result.get(0));
    System.out.format("Probability of fraud (1)     = %.3f\n", result.get(1));
  }

  @Deactivate
  public void deactivate () {
  }

}
