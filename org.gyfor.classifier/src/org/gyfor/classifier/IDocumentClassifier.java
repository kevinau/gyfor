package org.gyfor.classifier;

import org.gyfor.docstore.IDocumentContents;

public interface IDocumentClassifier {

  public int classify(IDocumentContents docContents);

  public void train(IDocumentContents docContents, int category);

}
