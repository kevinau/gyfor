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

  private String name;

  private String knownAs;

  private String phoneNumber;

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

  @Label(description = "The full name of the person", hint = "Typically first name and surname")
  public void setName(String name) {
    this.name = name;
  }

  @ItemField(length = 32)
  @Optional
  @Label(description = "How to address this person", hint = "First name only")
  public void setKnownAs(String knownAs) {
    this.knownAs = knownAs;
  }

  @ItemField(type = PhoneNumberType.class)
  @Label(hint = "Typically a mobile phone number")
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @ItemField(type = PhoneNumberType.class)
  @Optional
  public void setPhoneNumber2(String phoneNumber2) {
    this.phoneNumber2 = phoneNumber2;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

}
