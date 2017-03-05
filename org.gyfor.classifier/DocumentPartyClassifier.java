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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
@Component(immediate = true)
public class DocumentPartyClassifier {

  /**
   * Constructs empty training dataset.
   */
  @Activate
  public void activate(ComponentContext componentComtext) {
    // Get all known documents
    List<Document> docs = new ArrayList<>();
    PrimaryIndex<String, Document> primaryIndex = docStore.getPrimaryIndex();
    try (EntityCursor<Document> cursor = primaryIndex.entities()) {
      for (Document doc : cursor) {
        docs.add(doc);
      }
    }
    System.out.println("Found " + docs.size() + " documents");
    Comparator<Document> keyComparator = new Comparator<Document>() {
      @Override
      public int compare(Document arg0, Document arg1) {
        return arg0.getOriginTime().compareTo(arg1.getOriginTime());
      }
    };
    Collections.sort(docs, keyComparator);

    // For the moment, get a list of known party names
    List<String> partyNames = new ArrayList<>();
    for (Document doc : docs) {
      String partyName = doc.getOriginName().substring(0, 3);
      if (!partyNames.contains(partyName)) {
        partyNames.add(partyName);
        System.err.println(partyName);
      }
    }
    System.out.println(partyNames);
    
    NaiveBayesClassifier classifier = new NaiveBayesClassifier(partyNames);

    int[] partyCounts = new int[partyNames.size()];
    int testCount = 0;
    int failCount = 0;
    for (Document doc : docs) {
      // Try to classify the document
      String partyName = doc.getOriginName().substring(0, 3);
      int n = partyNames.indexOf(partyName);
      
      testCount++;
      if (classifier.isEmpty()) {
        System.err.println("Message " + doc.getId() + " " + doc.getOriginName() + " cannot be classified as classifier is empty");
        failCount++;
      } else {
        double prediction;
        if (classifier.numClasses() == 1) {
          prediction = 0;
        } else {
          try {
            prediction = classifier.classifyMessage(doc);
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
          String predictionx = classifier.getClassValue(prediction);
          if (predictionx.equals(partyName)) {
            //System.out.println("Message " + doc.getId() + " " + doc.getOriginName() + " classified as : " + predictionx);
          } else {
            int actualCount = partyCounts[n];
            int predictedCount = partyCounts[(int)prediction];
            System.err.println("Message " + doc.getId() + " " + doc.getOriginName() + " MIS-classified as : " + predictionx + "(" + actualCount + "," + predictedCount + "}");
            failCount++;
          }
        }
      }
      partyCounts[n]++;
      classifier.trainClassifier(doc, partyName);
    }
    if (testCount > 0) {
      System.out.println(failCount + " failures out of " + testCount + " tests (" + (failCount * 100 / testCount) + "%)");
    }
    System.out.println("Dictionary size " + classifier.getDictionarySize());
  }


  @Deactivate
  public void deactivate() {
  }

}
