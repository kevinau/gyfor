package org.gyfor.doc;

import org.gyfor.srcdoc.SourceDocument;

public interface DocumentStoreListener {

  public void documentAdded(SourceDocument doc);
  
  public void documentRemoved(SourceDocument doc);
  
}
