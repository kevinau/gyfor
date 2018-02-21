package org.gyfor.dao.test.data;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityLabel;
import org.gyfor.object.ItemField;
import org.gyfor.object.Label;
import org.gyfor.object.Optional;
import org.gyfor.object.SelfDescribing;
import org.gyfor.object.UniqueConstraint;
import org.gyfor.object.type.builtin.PhoneNumberType;
import org.gyfor.value.VersionTime;

import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;


@Entity
@com.sleepycat.persist.model.Entity
@UniqueConstraint({ "shortName" })
@EntityLabel(description = "A person or business that you deal with")
public class Party implements SelfDescribing {

  @PrimaryKey(sequence = "Party_seq")
  private int id;
  
  private VersionTime versionTime;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String shortName;

  private String formalName;

  private String webPage;
  
  private String phoneNumber;

  public Party() {
    this.shortName = "";
    this.formalName = "";
  }

  public Party(String shortName, String formalName, String webPage) {
    this.shortName = shortName;
    this.formalName = formalName;
    this.webPage = webPage;
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
    return "Party[" + id + "," + versionTime + "," + shortName + "," + formalName + "," + webPage + "]";
  }
  
  public int getId() {
    return id;
  }

  public String getShortName() {
    return shortName;
  }

  @ItemField(length = 16)
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

  @Optional
  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Optional
  @ItemField(type = PhoneNumberType.class)
  @Label("Primary phone number")
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

}
