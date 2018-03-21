package org.plcore.sql;

public class SQLExecuteException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  
  public SQLExecuteException (java.sql.PreparedStatement stmt, Throwable ex) {
    super (stmt.toString(), ex);
  }
  
  public SQLExecuteException (java.sql.PreparedStatement stmt) {
    super (stmt.toString());
  }
  
  public SQLExecuteException (Throwable ex) {
    super (ex);
  }
  
}
