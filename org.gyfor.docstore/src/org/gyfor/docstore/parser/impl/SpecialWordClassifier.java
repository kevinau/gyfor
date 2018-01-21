/*
 * Copyright (c) 2004-2009, Jean-Marc Fran√ßois. All Rights Reserved.
 * Licensed under the New BSD license.  See the LICENSE file.
 */

package org.gyfor.docstore.parser.impl;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.ITrainingData;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.gyfor.srcdoc.Dictionary;
import org.gyfor.srcdoc.ISegment;
import org.gyfor.srcdoc.SegmentType;

public class SpecialWordClassifier {
  
  private static class SegmentXComparator implements Comparator<ISegment> {

    /* One point at nominated dpi */
    private static final double POINT = IDocumentStore.IMAGE_RESOLUTION / 72.0;
    
    @Override
    public int compare(ISegment arg0, ISegment arg1) {
      float xdiff = arg0.getX0() - arg1.getX0();
      if (xdiff > -POINT && xdiff < POINT) {
        // The x coordinates are close enough to be the same
        if (arg0.getY0() < arg1.getY0()) {
          return -1;
        } else if (arg0.getY0() > arg1.getY0()) {
          return +1;
        } else {
          return 0;
        }
      }
      if (arg0.getX0() < arg1.getX0()) {
        return -1;
      } else {
        return +1;
      }
    }
        
  }
  
  
  private static class SegmentYComparator implements Comparator<ISegment> {

    /* One point at nominated dpi */
    private static final double POINT = IDocumentStore.IMAGE_RESOLUTION / 72.0;
    
    @Override
    public int compare(ISegment arg0, ISegment arg1) {
      float ydiff = arg0.getY0() - arg1.getY0();
      if (ydiff > -POINT && ydiff < POINT) {
        // The y coordinates are close enough to be the same
        if (arg0.getX0() < arg1.getX0()) {
          return -1;
        } else if (arg0.getX0() > arg1.getX0()) {
          return +1;
        } else {
          return 0;
        }
      }
      if (arg0.getY0() < arg1.getY0()) {
        return -1;
      } else {
        return +1;
      }
    }
    
  }
  
  
  private final IImageParser imageParser;
  private final IPDFParser pdfParser;

  private Dictionary dictionary;
  
//  static public void main(String[] argv) throws java.io.IOException {
//    
//    IImageParser imageParser = new TesseractImageOCR();
//    IPDFParser pdfParser = new PDFBoxPDFParser(imageParser);
//
//    SpecialWordClassifier hmmTest = new SpecialWordClassifier(imageParser, pdfParser);
//
//    hmmTest.run();
//  }
  
  
  public SpecialWordClassifier (IImageParser imageParser, IPDFParser pdfParser) {
    this.imageParser = imageParser;
    this.pdfParser = pdfParser;
  }
  
  
//  private void runx() {
//    DocumentContentsBuilder contentsBuilder = new DocumentContentsBuilder(pdfParser, imageParser);
//
//    Path dir = Paths.get("C:/Users/Kevin/Accounts/JH Shares/Telstra");
//    String pattern = "2012-03-23.pdf";
//
//    dictionary = new Dictionary();
//    dictionary.setReadOnly(false);
//    
//    List<Path> trainingSources = new ArrayList<>();
//    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, pattern)) {
//      for (Path fileEntry : stream) {
//        Path digestNamedPath = SourcePath.createDigestNamed(fileEntry);
//        FileIO.conditionallyCopyFile(fileEntry, digestNamedPath);
//
//        IDocumentContents doc = contentsBuilder.buildContent(digest, false);
//        
//        Path dataPath = new SourcePath(digestNamedPath).getDataPath();
//        ITrainingData trainingData = ITrainingData.load(dataPath, DividendStatement.class);
//        doc.updateDictionary(dictionary, trainingData);
//        
//        trainingSources.add(digestNamedPath);
//      }
//    } catch (IOException ex) {
//      throw new RuntimeException(ex);
//    }
//
//    dictionary.resolve("?");        // dates
//    dictionary.resolve("§");        // money amounts
//    dictionary.setReadOnly(true);
//    
//    /* Build observation sequences (observationSet) */
//    List<List<Integer>> observationSet = new ArrayList<>();
//    
//    for (Path hashNamedPath : trainingSources) {
//      Path contentsPath = new SourcePath(hashNamedPath).getContentsPath();
//      IDocumentContents doc = IDocumentContents.load(contentsPath);
//      doc.sortSegments();
//      
//      Path dataPath = new SourcePath(hashNamedPath).getDataPath();
//      ITrainingData trainingData = ITrainingData.load(dataPath, DividendStatement.class);
//
//      System.out.println();
//      System.out.println(hashNamedPath.getFileName().toString());
//      System.out.println("Dictionary size " + dictionary.size());
// 
//      List<? extends ISegment> segmentsAcross = doc.getSegments();
//      List<? extends ISegment> segmentsDown = new ArrayList<>(segmentsAcross);
//      Collections.sort(segmentsAcross, new SegmentXComparator());
//      Collections.sort(segmentsDown, new SegmentYComparator());
//      
////      System.out.print("Left to right: ");
////      for (ISegment obv : segmentsAcross) {
////        String w = obv.getText();
////        System.out.print(w);
////        System.out.print('¨');
////      }
////      System.out.println();
////      System.out.print("Down: ");
////      for (ISegment obv : segmentsDown) {
////        String w = obv.getText();
////        System.out.print(w);
////        System.out.print('¨');
////      }
////      System.out.println();
//
//      findSpecials(SegmentType.DATE, segmentsAcross, segmentsDown, trainingData, observationSet);
//      findSpecials(SegmentType.CURRENCY, segmentsAcross, segmentsDown, trainingData, observationSet);
//    }
//    
//  }

  
  private void findSpecials (SegmentType segType, List<? extends ISegment> segmentsAcross, List<? extends ISegment> segmentsDown, ITrainingData trainingData, List<List<Integer>> observationSet) {
    for (int t = 0; t < segmentsDown.size(); t++) {
      ISegment special = segmentsDown.get(t);
      if (special.getType() == segType) {
        String specialWord = trainingData.resolveValue(segType, special.getValue());
        if (specialWord != null) {
          // Walk backwards to find overlapping segments
          LinkedList<Integer> observationsLeft = leftwiseOverlaps(special, segmentsAcross);
          LinkedList<Integer> observationsUp = upwardOverlaps(special, segmentsDown);
          //List<ObservationInteger> observationSequence = interlaceObservations(observationsLeft, observationsUp);

          // Add the special observation to the end of the observation list
          int wordIndex = dictionary.getWordIndex(specialWord);
          observationsLeft.add(wordIndex);
          observationsUp.add(wordIndex);
          
          observationSet.add(observationsLeft);
          observationSet.add(observationsUp);
          for (Integer obv : observationsLeft) {
            int wi = obv;
            String w = dictionary.getWord(wi);
            System.out.print(w);
            System.out.print('|');
          }
          System.out.println();
          for (Integer obv : observationsUp) {
            int wi = obv;
            String w = dictionary.getWord(wi);
            System.out.print(w);
            System.out.print('^');
          }
          System.out.println();
        }
      }
    }
  }

  
  private LinkedList<Integer> leftwiseOverlaps (ISegment special, List<? extends ISegment> segmentsAcross) {
    LinkedList<Integer> found = new LinkedList<>();
    
    float x0 = special.getX0();
    for (int i = segmentsAcross.size() - 1; i >= 0; i--) {
      ISegment prior = segmentsAcross.get(i);
      //System.out.println("--- " + i + " " + prior + " " + prior.overlapsHorizontally(special));
      if (prior.getX0() < x0 && prior.overlapsHorizontally(special)) {
        String word;
        // Anonymize other special segments 
        switch (prior.getType()) {
        case DATE :
          word = "?";
          break;
        case CURRENCY :
          word = "§";
          break;
        default :
          word = prior.getText();
          break;
        }
        int wordIndex = dictionary.getWordIndex(word);
        if (wordIndex != -1) {
          found.addFirst(wordIndex);
        }
      }
    }
    return found;
  }
  
    
  private LinkedList<Integer> upwardOverlaps (ISegment special, List<? extends ISegment> segmentsDown) {
    LinkedList<Integer> found = new LinkedList<>();
    
    float y0 = special.getY0();
    for (int j = segmentsDown.size() - 1; j >= 0; j--) {
      ISegment prior = segmentsDown.get(j);
      //System.out.println("||| " + j + " " + prior + " " + prior.overlapsVertically(special));
      if (prior.getY0() < y0 && prior.overlapsVertically(special)) {
        String word;
        // Anonymize other special segments 
        switch (prior.getType()) {
        case DATE :
          word = "?";
          break;
        case CURRENCY :
          word = "§";
          break;
        default :
          word = prior.getText();
          break;
        }
        int wordIndex = dictionary.getWordIndex(word);
        if (wordIndex != -1) {
          found.addFirst(wordIndex);
        }
      }
    }
    return found;
  }
  
  
//  private List<ObservationInteger> interlaceObservations (LinkedList<ObservationInteger> leftwiseObservations, LinkedList<ObservationInteger> upwardObservations) {
//    LinkedList<ObservationInteger> combined = new LinkedList<>();
//    
//    Iterator<ObservationInteger> i1 = leftwiseObservations.descendingIterator();
//    Iterator<ObservationInteger> i2 = upwardObservations.descendingIterator();
//    while (i1.hasNext() && i2.hasNext()) {
//      combined.addFirst(i1.next());
//      combined.addFirst(i2.next());
//    }
//    while (i1.hasNext()) {
//      combined.addFirst(i1.next());
//    }
//    while (i2.hasNext()) {
//      combined.addFirst(i2.next());
//    }
//    return combined;
//  }

  
//  Hmm<ObservationInteger> buildHmm (int dictSize, int targetsCount, List<List<ObservationInteger>> observationSet) {
//    Hmm<ObservationInteger> hmm = new Hmm<ObservationInteger>(targetsCount, new OpdfIntegerFactory(targetsCount));
//    
//    // Load Pi values for each final state
//    int[] targetCounts = new int[dictSize];
//    int overallCount = 0;
//    for (List<ObservationInteger> observationSequence : observationSet) {
//      int n = observationSequence.size();
//      int target = observationSequence.get(n - 1).value;
//      targetCounts[target]++;
//      overallCount++;
//    }
//    for (int i = 0; i < dictSize; i++) {
//      hmm.setPi(i, ((double)targetCounts[i]) / overallCount);
//    }
//    targetCounts = null;
//    
//    // Load the transition probabilities
//    int[][] transitionCounts = new int[dictSize][dictSize];
//    overallCount = 0;
//    for (List<ObservationInteger> observationSequence : observationSet) {
//      Iterator<ObservationInteger> it = observationSequence.iterator();
//      int i = it.next().value;
//      while (it.hasNext()) {
//        int j = it.next().value;
//        transitionCounts[i][j]++;
//        overallCount++;
//      }
//    }
//    for (int i = 0; i < dictSize; i++) {
//      for (int j = 0; j < dictSize; j++) {
//        hmm.setAij(i, j, ((double)transitionCounts[i][j]) / overallCount);
//      }
//    }
//
//    int[][] counts = new int[dictSize][dictSize];
//    overallCount = 0;
//    for (List<ObservationInteger> observationSequence : observationSet) {
//      Iterator<ObservationInteger> it = observationSequence.iterator();
//      int from = it.next().value;
//      while (it.hasNext()) {
//        int to = it.next().value;
//        counts[from][to]++;
//        overallCount++;
//      }
//    }
//    
//
////    hmm.setPi(0, 0.95);
////    hmm.setPi(1, 0.05);
////    
////    hmm.setOpdf(0, new OpdfDiscrete<Packet>(Packet.class, 
////        new double[] { 0.95, 0.05 }));
////    hmm.setOpdf(1, new OpdfDiscrete<Packet>(Packet.class,
////        new double[] { 0.20, 0.80 }));
////    
////    hmm.setAij(0, 1, 0.05);
////    hmm.setAij(0, 0, 0.95);
////    hmm.setAij(1, 0, 0.10);
////    hmm.setAij(1, 1, 0.90);
//    
//    return hmm;
//  }

}
