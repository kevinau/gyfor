package org.gyfor.party;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.object.SelfDescribing;
import org.gyfor.object.value.EntityLife;


public class Party implements SelfDescribing {

  private final int id;
  
  private String companyNumber;
  
  private String formalName;
  
  private String shortName;
  
  private EntityLife entityLife;
  
  
  public Party () {
    this.id = 0;
    this.companyNumber = null;
    this.formalName = null;
    this.shortName = null;
    this.entityLife = EntityLife.ACTIVE;
  }
  
  
  public Party (int id, String companyNumber, String formalName) {
    this.id = id;
    this.companyNumber = companyNumber;
    this.formalName = formalName;
    this.shortName = null;
    this.entityLife = EntityLife.ACTIVE;
  }
  
  
  public Party (int id, String companyNumber, String formalName, String shortName) {
    this.id = id;
    this.companyNumber = companyNumber;
    this.formalName = formalName;
    this.shortName = shortName;
    this.entityLife = EntityLife.ACTIVE;
  }
  
  
  public String getCompanyNumber () {
    return companyNumber;
  }
  
  
  public String getFormalName () {
    return formalName;
  }
  
  
  public String getShortName () {
    return shortName;
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
    if (shortName != null) {
      // Append the short name, unless it is already contained within the description
      Pattern pattern = Pattern.compile("\\b" + shortName + "\\b");
      Matcher matcher = pattern.matcher(description);
      if (!matcher.find()) {
        description += " (" + shortName + ")";
      }
    }
    return description;
  }
  
  
  @Override
  public String toString() {
    return "Party[" + companyNumber + ", " + shortName + ", " + formalName + "]";
  }

}
