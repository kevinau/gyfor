package org.gyfor.berkeleydb.test;

import java.util.Date;

public class FileDetails {

  private String name;
  
  private Date modified;
  
  private int size;
  
  public FileDetails () {
  }
  
  
  public FileDetails (String name, Date modified, int size) {
    this.name = name;
    this.modified = modified;
    this.size = size;
  }


  @Override
  public String toString() {
    return "FileDetails [" + name + ", " + modified + ", " + size + "]";
  }
  
}
