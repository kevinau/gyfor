package org.gyfor.home;

import java.nio.file.Path;

public interface IApplication {

  public String getId();
  
  public Path getBaseDir();
  
}