package org.gyfor.dao.test.data;

public class SimpleEntity {

  private int id;
  
  //private VersionTime version;
  
  private String code;
  
  private String description;
  
  public SimpleEntity () {
  }
  
  public SimpleEntity (String code, String description) {
    this.code = code;
    this.description = description;
  }

  @Override
  public String toString() {
    return "SimpleEntity[" + id + "," + code + "," + description + "]";
  }
  
}
