package org.plcore.docstore.parser;

import java.nio.file.Path;

import org.plcore.srcdoc.ISourceDocumentContents;

public interface IImageParser {

  public ISourceDocumentContents parse (String id, int pageIndex, Path path);
  
}
