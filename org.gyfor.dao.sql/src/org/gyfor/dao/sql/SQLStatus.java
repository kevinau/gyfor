package org.gyfor.dao.sql;

import java.io.Serializable;
import java.sql.Timestamp;

public class SQLStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  private final int id;
  private final Timestamp version;

  public SQLStatus (int id, Timestamp version) {
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
