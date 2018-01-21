package org.pennyledger.party;

import org.gyfor.object.Entity;
import org.gyfor.object.Label;

@Entity
public class Address {

  private String unannotatedField;
  
  private String noLabelField;
  
  private String field2;
  
  private String field3;

  private String field4;
  
  private String field5;

  
  public String getUnannotatedField() {
    return unannotatedField;
  }

  
  public void setUnannotatedField(String unannotatedField) {
    this.unannotatedField = unannotatedField;
  }

  
  public String getNoLabelField() {
    return noLabelField;
  }

  
  @Label("")
  public void setNoLabelField(String noLabelField) {
    this.noLabelField = noLabelField;
  }

  
  public String getField2() {
    return field2;
  }

  
  @Label("Simple label")
  public void setField2(String field2) {
    this.field2 = field2;
  }

  
  public String getField3() {
    return field3;
  }

  
  @Label(hint="If required")
  public void setField3(String field3) {
    this.field3 = field3;
  }

  
  public String getField4() {
    return field4;
  }

  
  @Label(description="The town or suburb of the address")
  public void setField4(String field4) {
    this.field4 = field4;
  }

  
  public String getField5() {
    return field5;
  }

  
  @Label(value="Fully labeled", hint="Label hint", description="This is a description of the item")
  public void setField5(String field5) {
    this.field5 = field5;
  }
  
}
