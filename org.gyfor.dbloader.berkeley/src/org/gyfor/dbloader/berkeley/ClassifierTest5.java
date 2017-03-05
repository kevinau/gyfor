package org.gyfor.dbloader.berkeley;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.gyfor.classifier.IDocumentClassifier;
import org.gyfor.docstore.DocumentStore;
import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.gyfor.docstore.parser.impl.PDFBoxPDFParser;
import org.gyfor.docstore.parser.impl.TesseractImageOCR;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true)
public class ClassifierTest5 {

  private IDocumentClassifier partyClassifier;
  
  @Reference (target = "(name=party)")
  protected void setPartyClassifier (IDocumentClassifier partyClassifier) {
    this.partyClassifier = partyClassifier;
  }
  
  protected void unsetPartyClassifier (IDocumentClassifier partyClassifier) {
    this.partyClassifier = partyClassifier;
  }

  
  @Activate
  public void activate() {
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
    List<String> partyNames = new ArrayList<>();

    int totalSamples = 0;
    int matched1 = 0;
    
    int m = 0;
    for (File file : files) {
      Path path = file.toPath();
      System.out.println();
      System.out.println(path);

      String id = path.getFileName().toString();
      // Remove PDF extension
      id = id.substring(0, id.length() - 4);

      // Get the phrases used in this document
      IDocumentContents docContents = pdfParser.parseText(id, path, 600, docStore);
      int party = partyClassifier.classify(docContents);
      
      // Get party name from file name
      String fileName = file.getName();
      String partyName = fileName.substring(0, 3);
      int partyIndex = partyNames.indexOf(partyName);
      if (partyIndex == -1) {
        partyIndex = partyNames.size();
        partyNames.add(partyName);
      } else {
        if (partyIndex == party) {
          matched1++;
        }
        totalSamples++;
      }
      
      partyClassifier.train(docContents, partyIndex);
      m++;
      //System.out.println(">>>>>>>>>>>>> " + m);
      if (m > 1000) {
        break;
      }

    }
    
    System.out.println("Matched: " + matched1 + " out of " + totalSamples + " = " + (matched1 * 100.0 / totalSamples) + "%");
  }
    
}
