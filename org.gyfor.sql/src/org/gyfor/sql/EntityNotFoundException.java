package org.gyfor.sql;


public class EntityNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  
  public EntityNotFoundException(String tableName, Object keyValue) {
    super ("No " + tableName + " with primary key valye = " + keyValue.toString());
  }

}
