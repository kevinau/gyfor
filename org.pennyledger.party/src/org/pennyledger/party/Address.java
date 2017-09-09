package org.pennyledger.party;

import org.gyfor.object.Entity;
import org.gyfor.object.Label;

@Entity
public class Address {

  @SuppressWarnings("unused")
  private String unannotatedField;
  
  @Label("")
  private String noLabelField;
  
  @Label("Simple label")
  private String field2;
  
  @Label(hint="If required")
  private String field3;

  @Label(description="The town or suburb of the address")
  private String field4;
  
  @Label(value="Fully labeled", hint="Label hint", description="This is a description of the item")
  private String field5;
  
}
