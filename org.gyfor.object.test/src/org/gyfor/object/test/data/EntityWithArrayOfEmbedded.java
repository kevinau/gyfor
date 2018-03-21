package org.gyfor.object.test.data;

import org.gyfor.math.Decimal;
import org.plcore.userio.Embeddable;
import org.plcore.userio.IOField;

@SuppressWarnings("unused")
public class EntityWithArrayOfEmbedded {

  @Embeddable
  private static class Inner {
    @IOField
    private String field1;
    
    @IOField
    private Decimal field2;
    
    public Inner() {
    }
    
    public Inner(String field1, Decimal field2) {
      this.field1 = field1;
      this.field2 = field2;
    }
  }
  
  
  @IOField
  private int id;
  
  @IOField
  private String field1;
  
  @IOField
  private Inner[] inner;

  public EntityWithArrayOfEmbedded () {
  }

  
  public EntityWithArrayOfEmbedded (String field1, String inner1, Decimal inner2, String inner1x, Decimal inner2x) {
    this.id = 1;
    this.field1 = field1;
    this.inner = new Inner[] {
        new Inner(inner1, inner2),
        new Inner(inner1x, inner2x),
    };
  }

}
