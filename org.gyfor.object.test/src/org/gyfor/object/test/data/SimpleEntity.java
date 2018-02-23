package org.gyfor.object.test.data;

import org.gyfor.math.Decimal;
import org.gyfor.object.IOField;
import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;

@SuppressWarnings("unused")
public class SimpleEntity {

  @IOField
  private int id;

  @IOField
  private VersionTime version;
  
  @IOField
  private EntityLife entityLife;
  
  @IOField
  private String field1;
  
  @IOField
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
