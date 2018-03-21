package org.plcore.sql;


public class RowNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  
  public RowNotFoundException(String tableName, Object keyValue) {
    super ("No " + tableName + " with primary key value = " + keyValue.toString());
  }

}
