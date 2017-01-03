package org.gyfor.docstore;

import org.gyfor.object.Entity;
import org.gyfor.object.Id;
import org.gyfor.object.UniqueConstraint;


@Entity
@UniqueConstraint("partyCode")
public class Party {

  @Id
  private int id;
  
  private String partyCode;
  
  private String shortName;
  
  private String formalName;
  
  private String webPage;
  
  public Party () {
  }
  
  
  public Party (int id, String partyCode, String shortName, String formalName, String webPage) {
    this.id = id;
    this.partyCode = partyCode;
    this.shortName = shortName;
    this.formalName = formalName;
    this.webPage = webPage;
  }
  
  
  @Override
  public String toString() {
    return "Party[" + id + ", " + partyCode + ", " + shortName + ", " + formalName + ", " + webPage + "]";
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
  
  
  public String getWebPage () {
    return webPage;
  }

}
