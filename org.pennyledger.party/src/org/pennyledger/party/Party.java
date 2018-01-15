package org.pennyledger.party;

import java.io.Serializable;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityLabel;
import org.gyfor.object.SelfDescribing;
import org.gyfor.object.UniqueConstraint;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;


@Entity
@UniqueConstraint({"partyCode", "shortName"})
@EntityLabel (value="Business Entity")
public class Party implements SelfDescribing, Serializable {

  private static final long serialVersionUID = 1L;

  private int id;
  
  private VersionTime version;
  
  private String partyCode;
  
  private String shortName;
  
  private String formalName;
  
  private String webPage;
  
  private EntityLife entityLife;
  
  public Party () {
  }
  
  
  public Party (int id, String partyCode, String shortName, String formalName, String webPage) {
    this.id = id;
    this.partyCode = partyCode;
    this.shortName = shortName;
    this.formalName = formalName;
    this.webPage = webPage;
  }
  
  
  public Party (Party old) {
    this.id = old.id;
    this.partyCode = old.partyCode;
    this.shortName = old.shortName;
    this.formalName = old.formalName;
    this.webPage = old.webPage;
  }
  
  
  public int getId () {
    return id;
  }
  
  
  @Override
  public String getDescription () {
    String description;
    String suffix = " Limited";
    if (formalName.endsWith(suffix)) {
      description = formalName.substring(0,  formalName.length() - suffix.length());
    } else {
      description = formalName;
    }
    description += " (" + shortName + ")";
    return description;
  }
  
  
  @Override
  public String toString() {
    return "Party[" + id + ", " + partyCode + ", " + shortName + ", " + formalName + ", " + webPage + ", " + version + ", " + entityLife + "]";
  }


  public String getPartyCode () {
    return partyCode;
  }
  
  
  public String getShortName () {
    return shortName;
  }
  
  
  public String getFormalName () {
    return formalName;
  }
  
  
  public void setFormalName (String formalName) {
    this.formalName = formalName;
  }
  
  
  public String getWebPage () {
    return webPage;
  }

  
  public void setWebPage (String webPage) {
    this.webPage = webPage;
  }
  
}
