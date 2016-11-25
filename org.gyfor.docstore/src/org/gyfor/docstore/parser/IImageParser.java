package org.gyfor.docstore.parser;

import java.nio.file.Path;

import org.gyfor.util.Digest;
import org.gyfor.docstore.IDocumentContents;

public interface IImageParser {

  public IDocumentContents parse (Digest digest, Path path);
  
}
