package org.gyfor.docstore.parser;

import java.nio.file.Path;

import org.gyfor.docstore.IDocumentStore;
import org.gyfor.srcdoc.ISourceDocumentContents;


public interface IPDFParser {

  public ISourceDocumentContents parse (Path path, int dpi);

  public ISourceDocumentContents parse (String id, Path path, int dpi, IDocumentStore docStore);

  public ISourceDocumentContents parseText(String id, Path pdfPath, int dpi, IDocumentStore docStore);
  
}
