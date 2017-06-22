package org.gyfor.object.test.data;

import org.gyfor.math.Decimal;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;

public class SimpleEntity {

  private int id;

  private VersionTime version;
  
  private EntityLife entityLife;
  
  private String field1;
  
  private Decimal field2;
  
  public SimpleEntity () {
  }
  
  
  public SimpleEntity (String field1, Decimal field2) {
    this.id = 1;
    this.version = VersionTime.now();
    this.entityLife = EntityLife.ACTIVE;
    this.field1 = field1;
    this.field2 = field2;
  }
  
}
