package org.gyfor.docstore.parser;

import java.nio.file.Path;

import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.IDocumentStore;


public interface IPDFParser {

  public IDocumentContents parse (String id, Path path, int dpi, IDocumentStore docStore);

  public IDocumentContents parseText(String id, Path pdfPath, int dpi, IDocumentStore docStore);
  
}
