package org.gyfor.docstore.parser;

import java.nio.file.Path;

import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.IDocumentStore;

public interface IPDFParser {

  public IDocumentContents parse (String id, Path path, int dpi, IDocumentStore docStore);

  public IDocumentContents parseText(String id, Path pdfPath, int dpi, IDocumentStore docStore);
  
}
