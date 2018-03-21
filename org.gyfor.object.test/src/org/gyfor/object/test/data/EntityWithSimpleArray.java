package org.gyfor.object.test.data;

import org.plcore.userio.IOField;

public class EntityWithSimpleArray {

  @IOField
  private int id;
  
  @IOField
  private String field1;
  
  @IOField
  private String[] field2;
  
  public EntityWithSimpleArray () {
  }
  
  
  public EntityWithSimpleArray (String field1, String... field2) {
    this.field1 = field1;
    this.field2 = field2;
  }
  
}
