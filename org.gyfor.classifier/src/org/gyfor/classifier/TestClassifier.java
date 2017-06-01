package org.gyfor.classifier;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.IDocumentStore;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.impl.PDFBoxPDFParser;
import org.gyfor.docstore.parser.impl.TesseractImageOCR;

public class TestClassifier {

  public void run () {
    File sourceDir = new File("C:/Users/Kevin/Scanned Stuff 2");
    File[] sourceFiles = sourceDir.listFiles();
    Comparator<File> fileComparator = new Comparator<File>() {
      @Override
      public int compare(File arg0, File arg1) {
        return Long.compare(arg0.lastModified(), arg1.lastModified());
      }
    };
    Arrays.sort(sourceFiles, fileComparator);

    IDocumentContents[] docContents = new IDocumentContents[sourceFiles.length];
    String[] docCategories = new String[docContents.length];
    Set<String> globalCategories = new HashSet<>();
    
    IImageParser imageParser = new TesseractImageOCR();
    PDFBoxPDFParser pdfParser = new PDFBoxPDFParser(imageParser);
    for (int n = 0; n < docContents.length; n++) {
      System.out.println(sourceFiles[n]);
      docContents[n] = pdfParser.parse(sourceFiles[n].toPath(), IDocumentStore.IMAGE_RESOLUTION);
      String category = sourceFiles[n].getName().substring(0, 3);
      docCategories[n] = category;
    }

    
    int matched = 0;
    for (int k = 0; k < docContents.length; k++) {
      System.out.println("Pass " + k);
      IDocumentClassifier classifier = new LatentSemanticAnalysisClassifier();

      for (int n = 0; n < k - 1; n++) {
        //classifier.classify(docContents[n]);
        classifier.train(docContents[n], docCategories[n]);
      }
      String predicted = classifier.classify(docContents[k]);
      System.out.print("Expected " + docCategories[k] + ", classified as " + predicted);
      if (docCategories[k].equals(predicted)) {
        matched++;
      } else {
        if (globalCategories.contains(docCategories[k])) {
          System.out.print(" ?????????????????????");
        } else {
          System.out.print(" !!!!!!!!!!!!!! (first seen)");          
        }
      }
      System.out.println();
      globalCategories.add(docCategories[k]);
    }
    System.out.println(matched + " matches out of " + docContents.length + " documents");
    double success = (matched + globalCategories.size()) * 100.0 / docContents.length;
    System.out.println(success + "% success rate");
  }
  
  public static void main (String[] args) {
    TestClassifier classifier = new TestClassifier();
    classifier.run();
  }
  
}
