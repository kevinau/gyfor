package org.gyfor.dao.test.data;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;


@Entity
public class SimpleEntity {

  @PrimaryKey(sequence = "SimpleEntity_seq")
  private int id;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String code;
  
  private String description;
  
  public SimpleEntity () {
  }
  
  public SimpleEntity (String code, String description) {
    this.code = code;
    this.description = description;
  }

  
  public String getCode() {
    return code;
  }

  
  public void setCode(String code) {
    this.code = code;
  }

  
  public String getDescription() {
    return description;
  }

  
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "SimpleEntity[" + id + "," + code + "," + description + "]";
  }
  
}
