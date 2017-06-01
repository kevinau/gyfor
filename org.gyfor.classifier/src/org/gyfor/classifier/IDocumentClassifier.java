package org.gyfor.classifier;

import org.gyfor.doc.IDocumentContents;

public interface IDocumentClassifier {

  public String classify(IDocumentContents docContents);

  public void train(IDocumentContents docContents, String category);

}
