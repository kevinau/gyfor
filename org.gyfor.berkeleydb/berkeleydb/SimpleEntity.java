package org.gyfor.berkeleydb;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

import java.io.Serializable;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;


@Entity
public class SimpleEntity implements Serializable {

  private static final long serialVersionUID = 2L;

  
  // Primary key is pKey
  @PrimaryKey
  private String pKey;

  // Secondary key is the sKey
  @SecondaryKey(relate = MANY_TO_ONE)
  private String sKey;


  public void setPKey(String data) {
    pKey = data;
  }


  public void setSKey(String data) {
    sKey = data;
  }


  public String getPKey() {
    return pKey;
  }


  public String getSKey() {
    return sKey;
  }
}
