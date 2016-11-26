package org.gyfor.docstore.parser;

import java.nio.file.Path;

import org.gyfor.docstore.IDocumentContents;

public interface IImageParser {

  public IDocumentContents parse (String id, Path path);
  
}
