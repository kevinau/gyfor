package org.gyfor.classifier;

import org.gyfor.doc.IDocumentContents;

public interface IDocumentClassifier {

  public int classify(IDocumentContents docContents);

  public void train(IDocumentContents docContents, int category);

}
