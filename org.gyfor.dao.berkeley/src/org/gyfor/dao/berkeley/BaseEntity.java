package org.gyfor.dao.berkeley;

import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;


@Persistent
public class BaseEntity {

  @PrimaryKey
  private int id;
  
  private VersionTime versionTime;
  
  private EntityLife entityLife;
  
  
  @Override
  public String toString() {
    return "{" + id + "," + versionTime + "," + entityLife + "}";
  }
}
