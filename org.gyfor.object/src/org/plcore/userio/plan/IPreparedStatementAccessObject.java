package org.plcore.userio.plan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;


public interface IPreparedStatementAccessObject {

  public void toStmt (PreparedStatement stmt, int[] i, Object instance);
 
  public void toStmt2 (PreparedStatement stmt, int[] i, Object value);
  
  public void toField (Object instance, ResultSet rs, int i);
  
  public void setValue (Object instance, Object value);
  
  public <X> X getValue (Object instance);

}
