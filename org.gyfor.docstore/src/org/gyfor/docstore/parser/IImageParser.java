package org.gyfor.docstore.parser;

import java.nio.file.Path;

import org.gyfor.doc.IDocumentContents;

public interface IImageParser {

  public IDocumentContents parse (String id, int pageIndex, Path path);
  
}
