package org.pennyledger.party;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityLabel;
import org.gyfor.object.ItemField;
import org.gyfor.object.Label;
import org.gyfor.object.Optional;
import org.gyfor.object.type.builtin.PhoneNumberType;

@Entity
@EntityLabel(value = "U3A Prospect membership application")
public class U3AProspectApplication {
  
  private String givenName;

  private String familyName;

  private String knownAs;
  
  private String primaryPhone;
  
  private String alternatePhone;
  
  private String emailAddress;
  
  private String emergencyContact;
  
  private String emergencyPhone;

  
  public String getGivenName() {
    return givenName;
  }

  
  @ItemField(length = 40)
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  
  public String getFamilyName() {
    return familyName;
  }

  
  @ItemField(length = 40)
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  
  public String getKnownAs() {
    return knownAs;
  }

  
  @ItemField(length = 40)
  @Optional
  @Label("Prefered to be known as")
  public void setKnownAs(String knownAs) {
    this.knownAs = knownAs;
  }

  
  public String getPrimaryPhone() {
    return primaryPhone;
  }

  
  @ItemField(type=PhoneNumberType.class)
  public void setPrimaryPhone(String primaryPhone) {
    this.primaryPhone = primaryPhone;
  }

  
  public String getAlternatePhone() {
    return alternatePhone;
  }

  
  @ItemField(type=PhoneNumberType.class)
  @Optional
  public void setAlternatePhone(String alternatePhone) {
    this.alternatePhone = alternatePhone;
  }

  
  public String getEmailAddress() {
    return emailAddress;
  }

  
  @Optional
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  
  public String getEmergencyContact() {
    return emergencyContact;
  }

  
  @ItemField(length = 40)
  @Optional
  public void setEmergencyContact(String emergencyContact) {
    this.emergencyContact = emergencyContact;
  }

  
  public String getEmergencyPhone() {
    return emergencyPhone;
  }

  
  @ItemField(type=PhoneNumberType.class)
  @Optional
  public void setEmergencyPhone(String emergencyPhone) {
    this.emergencyPhone = emergencyPhone;
  }
  
}
