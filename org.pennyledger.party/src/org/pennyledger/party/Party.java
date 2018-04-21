package org.pennyledger.party;

import java.io.Serializable;

import org.pennyledger.phone.PhoneNumberType;
import org.plcore.userio.Entity;
import org.plcore.userio.EntityLabel;
import org.plcore.userio.IOField;
import org.plcore.userio.Label;
import org.plcore.userio.Optional;
import org.plcore.userio.SelfDescribing;
import org.plcore.userio.UniqueConstraint;

import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;


@Entity
@com.sleepycat.persist.model.Entity
@UniqueConstraint({ "shortName" })
@EntityLabel(description = "A person or business that you deal with")
public class Party implements SelfDescribing, Serializable {

  private static final long serialVersionUID = 1L;

  @PrimaryKey(sequence = "Party_ID")
  private int id;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  @IOField(length = 16)
  private String shortName;

  private String formalName;

  @Optional
  private String webPage;
  
  @Optional
  @Label("Primary phone number")
  private String phoneNumber;

  public Party() {
    this.shortName = "";
    this.formalName = "";
  }

  public Party(String partyCode, String shortName, String formalName, String webPage, String phoneNumber) {
    this.shortName = shortName;
    this.formalName = formalName;
    this.webPage = webPage;
    this.phoneNumber = phoneNumber;
  }

  public Party(Party old) {
    this.shortName = old.shortName;
    this.formalName = old.formalName;
    this.webPage = old.webPage;
  }

  @Override
  public String invokeDescription() {
    String description;
    String suffix = " Limited";
    if (formalName.endsWith(suffix)) {
      description = formalName.substring(0, formalName.length() - suffix.length());
    } else {
      description = formalName;
    }
    description += " (" + shortName + ")";
    return description;
  }

  @Override
  public String toString() {
    return "Party[" + id + ", " + shortName + ", " + formalName + ", " + webPage + ", " + phoneNumber + "]";
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getFormalName() {
    return formalName;
  }

  public void setFormalName(String formalName) {
    this.formalName = formalName;
  }

  public String getWebPage() {
    return webPage;
  }

  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
