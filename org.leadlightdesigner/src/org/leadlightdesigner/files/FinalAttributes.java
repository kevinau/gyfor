package org.leadlightdesigner.files;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FinalAttributes {

  private String source;
  
  public FinalAttributes (Path path) {
    this.source = path.toString();
  }

  
  public Path getSource () {
    return Paths.get(source);
  }
  
}
