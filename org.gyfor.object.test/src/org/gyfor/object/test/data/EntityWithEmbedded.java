package org.gyfor.object.test.data;

import org.gyfor.math.Decimal;
import org.gyfor.object.Embeddable;

public class EntityWithEmbedded {

  @Embeddable
  private static class Inner {
    private String field1;
    
    private Decimal field2;
    
    public Inner() {
    }
    
    public Inner(String field1, Decimal field2) {
      this.field1 = field1;
      this.field2 = field2;
    }
  }
  
  
  private int id;
  
  private String field1;
  
  private Inner inner;

  public EntityWithEmbedded () {
  }

  
  public EntityWithEmbedded (String field1, String inner1, Decimal inner2) {
    this.id = 1;
    this.field1 = field1;
    this.inner = new Inner(inner1, inner2);
  }

}
