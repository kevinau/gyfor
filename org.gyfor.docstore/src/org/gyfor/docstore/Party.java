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
  private String abn;
  
  @SecondaryKey(relate=Relationship.ONE_TO_ONE)
  private String shortName;
  
  private String formalName;
  
  private String webPage;
  
}
