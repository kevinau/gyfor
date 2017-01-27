package org.gyfor.dao;

import java.io.Serializable;
import java.sql.Timestamp;

import org.gyfor.object.value.VersionValue;

public class DataAddStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int id;
  private final VersionValue version;

  public DataAddStatus (int id, VersionValue version) {
    this.id = id;
    this.version = version;
  }
  
  public int getId () {
    return id;
  }
  
  
  public VersionValue getVersion () {
    return version;
  }
  
}
