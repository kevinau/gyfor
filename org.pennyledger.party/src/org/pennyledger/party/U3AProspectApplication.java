package org.pennyledger.party;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityLabel;
import org.gyfor.object.ItemField;
import org.gyfor.object.Label;
import org.gyfor.object.Optional;
import org.gyfor.object.type.builtin.PhoneNumberType;

@Entity
@EntityLabel(title = "U3A Prospect membership application")
public class U3AProspectApplication {
  
  @ItemField(length = 40)
  private String givenName;

  @ItemField(length = 40)
  private String familyName;

  @ItemField(length = 40)
  @Optional
  @Label("Prefered to be known as")
  private String knownAs;
  
  @ItemField(type=PhoneNumberType.class)
  private String primaryPhone;
  
  @ItemField(type=PhoneNumberType.class)
  @Optional
  private String alternatePhone;
  
  @Optional
  private String emailAddress;
  
  @ItemField(length = 40)
  @Optional
  private String emergencyContact;
  
  @ItemField(type=PhoneNumberType.class)
  @Optional
  private String emergencyPhone;
  
}
