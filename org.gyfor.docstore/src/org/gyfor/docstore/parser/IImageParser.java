package org.gyfor.docstore.parser;

import java.nio.file.Path;

import org.gyfor.srcdoc.ISourceDocumentContents;

public interface IImageParser {

  public ISourceDocumentContents parse (String id, int pageIndex, Path path);
  
}
