package org.gyfor.object.test.data;


public class EntityWithSimpleArray {

  private int id;
  
  private String field1;
  
  private String[] field2;
  
  public EntityWithSimpleArray () {
  }
  
  
  public EntityWithSimpleArray (String field1, String... field2) {
    this.field1 = field1;
    this.field2 = field2;
  }
  
}
