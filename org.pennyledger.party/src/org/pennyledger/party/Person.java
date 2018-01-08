package org.pennyledger.party;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityLabel;
import org.gyfor.object.ItemField;
import org.gyfor.object.Label;
import org.gyfor.object.Optional;
import org.gyfor.object.type.builtin.PhoneNumberType;

@Entity
@EntityLabel(description = "A real person (not a business)")
public class Person {

  @Label(description = "The full name of the person", hint = "Typically first name and surname")
  private String name;
  
  @ItemField(length = 32)
  @Optional
  @Label(description = "How to address this person", hint = "First name only")
  private String knownAs;
  
  @ItemField(type=PhoneNumberType.class)
  @Label(hint = "Typically a mobile phone number")
  private String phoneNumber;
  
  @ItemField(type=PhoneNumberType.class)
  @Optional
  private String phoneNumber2;
  
  private String emailAddress;
  

  public Person() {
    this.name = "";
    this.knownAs = "";
    this.phoneNumber = "";
    this.phoneNumber2 = "";
    this.emailAddress = "";
  }
  

  public Person(String name, String knownAs, String phoneNumber, String phoneNumber2, String emailAddress) {
    this.name = name;
    this.knownAs = knownAs;
    this.phoneNumber = phoneNumber;
    this.phoneNumber2 = phoneNumber2;
    this.emailAddress = emailAddress;
  }

  
  public String getName() {
    return name;
  }

  public String getKnownAs() {
    return knownAs;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }
  
  public String getPhoneNumber2() {
    return phoneNumber2;
  }
  
  public String getEmailAddress() {
    return emailAddress;
  }
  
}
