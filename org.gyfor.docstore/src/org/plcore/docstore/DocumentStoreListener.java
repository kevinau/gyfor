package org.plcore.docstore;

import org.plcore.srcdoc.SourceDocument;

public interface DocumentStoreListener {

  public void documentAdded(SourceDocument doc);
  
  public void documentRemoved(SourceDocument doc);
  
}
