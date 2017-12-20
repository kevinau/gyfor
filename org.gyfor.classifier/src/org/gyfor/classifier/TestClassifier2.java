package org.gyfor.classifier;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gyfor.doc.Document;
import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.ISegment;

public class TestClassifier2 {

  private static class PhraseUse {
    private Set<String> phrases = new HashSet<>();
    
    public void addPhrase(String phrase) {
      phrases.add(phrase);
    }
  }
  

  private Set<String> getPhrases(Document doc) {
    Set<String> phrases = new HashSet<>();
    IDocumentContents docContents = doc.getContents();
    List<? extends ISegment> segments = docContents.getSegments();
    for (ISegment segment : segments) {
      switch (segment.getType()) {
      case CURRENCY :
      case DATE :
      case PERCENT :
        break;
      default :
        String phrase = segment.getText();
        phrases.add(phrase);
        break;
      }
    }
    return phrases;
  }

  
  public void run () {
    File catalogDir = new File("C:/Users/Kevin/docstore/catalog");
    File[] catalogFiles = catalogDir.listFiles();

    for (int n = 0; n < catalogFiles.length; n++) {
      File file = catalogFiles[n];
      System.out.println(file);
      Document doc = Document.load(file.toPath());
      
      String originName = doc.getOriginName();
      String category = originName.substring(0, 3);
      
      Set<String> phrases = getPhrases(doc);
      for (String p : phrases) {
        System.out.println(originName + ": " + p);
      }
      
    }
  }
  
  public static void main (String[] args) {
    TestClassifier2 classifier = new TestClassifier2();
    classifier.run();
  }
  
}
