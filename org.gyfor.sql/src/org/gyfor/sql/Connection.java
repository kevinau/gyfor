/*******************************************************************************
 * Copyright (c) 2008 Kevin Holloway (kholloway@geckosoftware.com.au).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 ******************************************************************************/
package org.gyfor.sql;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;

import org.gyfor.math.Decimal;


public class Connection implements IConnection {

  private final java.sql.Connection conn;
  private boolean withinTransaction = false;
 
  
  public Connection (java.sql.Connection conn) {
    this.conn = conn;
  }
  
  
//  static String toSQLName (String name) {
//    StringBuffer buffer = new StringBuffer();
//    boolean insertUnderscore = false;
//    int n = name.length();
//    for (int i = 0; i < n; i++) {
//      char c = name.charAt(i);
//      if (i == 0) {
//         buffer.append(Character.toLowerCase(c));
//      } else {
//        if (insertUnderscore && Character.isUpperCase(c)) {
//          buffer.append('_');
//          insertUnderscore = false;
//          buffer.append(Character.toLowerCase(c));
//        } else {
//          buffer.append(c);
//        }
//        if (!Character.isUpperCase(c)) {
//          insertUnderscore = true;
//        }
//           
//      }
//    }
//    return buffer.toString();
//  }
  

  static void setValue (java.sql.PreparedStatement stmt, int i, Object value) {
    try {
      if (value instanceof Decimal) {
        stmt.setBigDecimal(i, new BigDecimal(((Decimal)value).toString()));
      } else if (value instanceof LocalDate) {
        stmt.setDate(i, new java.sql.Date(((LocalDate)value).toEpochDay()));
      } else {
        stmt.setObject(i, value);
      }
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public IPreparedStatement prepareStatement (String sql) {
    try {
      java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
      return new PreparedStatement(sql, stmt);
    } catch (SQLException ex) {
      throw new RuntimeException(sql, ex);
    }
  }

  
  public void beginTransaction () {
    try {
      conn.setAutoCommit(false);
      withinTransaction = true;
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public void endTransaction () {
    if (withinTransaction) {
      rollback();
    }
  }

  
  @Override
  public void rollback () {
    try {
      conn.rollback();
      conn.setAutoCommit(true);
      withinTransaction = false;
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void setAutoCommit (boolean auto) {
    try {
      conn.setAutoCommit(auto);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void commit () {
    try {
      conn.commit();
      conn.setAutoCommit(true);
      withinTransaction = false;
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void close () {
    try {
      conn.close();
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public java.sql.Connection getUnderlyingConnection () {
    return conn;
  }
  
  
  @Override
  public void executeCommand (String sql) {
    try {
      java.sql.Statement stmt = conn.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (SQLException ex) {
      throw new RuntimeException(sql, ex);
    }
  }
  
  
  @Override
  public IDatabaseMetaData getMetaData() {
    try {
      return new DatabaseMetaData(conn.getMetaData());
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }
    
}
