package org.gyfor.docstore;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Party {

  @PrimaryKey(sequence="Party_Sequence")
  private int id;
  
  @SecondaryKey(relate=Relationship.ONE_TO_ONE)
  private String partyCode;
  
  @SecondaryKey(relate=Relationship.ONE_TO_ONE)
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
