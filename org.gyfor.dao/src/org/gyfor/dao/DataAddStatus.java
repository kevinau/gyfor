package org.gyfor.dao;

import java.io.Serializable;
import java.sql.Timestamp;

public class DataAddStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int id;
  private final Timestamp version;

  public DataAddStatus (int id, Timestamp version) {
    this.id = id;
    this.version = version;
  }
  
  public int getId () {
    return id;
  }
  
  
  public Timestamp getVersion () {
    return version;
  }
  
}
