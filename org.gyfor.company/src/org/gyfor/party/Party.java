package org.gyfor.party;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.object.SelfDescribing;


public class Party implements SelfDescribing {

  private final String companyNumber;
  
  private final String formalName;
  
  private final String shortName;
  
  
  public Party (String companyNumber, String formalName) {
    this.companyNumber = companyNumber;
    this.formalName = formalName;
    this.shortName = null;
  }
  
  
  public Party (String companyNumber, String formalName, String shortName) {
    this.companyNumber = companyNumber;
    this.formalName = formalName;
    this.shortName = shortName;
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
