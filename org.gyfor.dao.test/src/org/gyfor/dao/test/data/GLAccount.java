package org.gyfor.dao.test.data;

import org.gyfor.value.VersionTime;
import org.plcore.userio.Entity;
import org.plcore.userio.SelfDescribing;

@Entity
public class GLAccount implements SelfDescribing {

  public enum Type {
    INCOME("Income"),
    EXPENSE("Expense"),
    ASSET("Asset"),
    LIABILITY("Liablity"),
    OWNERS_EQUITY("Owners equity");
    
    private final String description;
    
    Type (String description) {
      this.description = description;
    }
    
    public String getDescription () {
      return description;
    }
    
  }
  

  private int id;
  
  private VersionTime version;
  
  private String name;
  
  private Type type;

  @Override
  public String buildDescription() {
    return name + " (" + type.getDescription() + ")";
  }
  
}
