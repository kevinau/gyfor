/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.object.value;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class TimestampValue {
  private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  private static final DateFormat sqlFormat = new SimpleDateFormat("'yyyy-MM-dd hh:mm:ss'");
  
  
  private final Timestamp t;

  
  public TimestampValue (Timestamp t) {
    this.t = t;
  }
  

  public Timestamp timestampValue () {
    return t;
  }
  
  
  public Timestamp objectValue () {
    return t;
  }
  
  
  @Override
  public String toString() {
    if (t == null) {
      return "";
    } else {
      return format.format(t);
    }
  }
  
  
  public String toSqlLiteral() {
    return sqlFormat.format(t);
  }


  public void setInStmt (PreparedStatement stmt, int[] index) {
    try {
      stmt.setTimestamp (index[0]++, t);
    } catch (SQLException ex) {
      throw new RuntimeException(ex);
    }
  }


  public String asPriorValue () {
  	return toString();
  }
  
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + t.hashCode();
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    TimestampValue other = (TimestampValue)obj;
    if (!t.equals(other.t)) {
      return false;
    }
    return true;
  }
}
