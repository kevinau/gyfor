package org.plcore.docstore.parser;

import java.nio.file.Path;

import org.plcore.docstore.IDocumentStore;
import org.plcore.srcdoc.ISourceDocumentContents;


public interface IPDFParser {

  public ISourceDocumentContents parse (Path path, int dpi);

  public ISourceDocumentContents parse (String id, Path path, int dpi, IDocumentStore docStore);

  public ISourceDocumentContents parseText(String id, Path pdfPath, int dpi, IDocumentStore docStore);
  
}
