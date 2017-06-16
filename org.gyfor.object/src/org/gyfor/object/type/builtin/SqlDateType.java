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
package org.gyfor.object.type.builtin;


import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


public class SqlDateType extends DateBasedType<Date> {
  
  public SqlDateType () {
  }
  

  @Override
  public Date primalValue() {
    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, 0);
    calendar.set(Calendar.MONTH, 0);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return new Date(calendar.getTimeInMillis());
  }


  @Override
  public Date today() {
    java.util.Date now = new java.util.Date();
    return new Date(now.getTime());
  }
  
  
  @Override
  protected void validate(Date date) throws UserEntryException {
    // Nothing more to validate
  }


  @Override
  protected Date createFromYearMonthDay(int year, int month, int day) {
    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
   return new Date(calendar.getTimeInMillis());
  }


  @Override
  protected int[] splitDate(Date date) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    int[] components = new int[3];
    components[0] = calendar.get(Calendar.YEAR);
    components[1] = calendar.get(Calendar.MONTH) + 1;
    components[2] = calendar.get(Calendar.DAY_OF_MONTH);
    return components;
  }


  @Override
  public java.sql.Date getFromBuffer (SimpleBuffer b) {
    long n = getLongFromBuffer(b);
    return new java.sql.Date(n);
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, java.sql.Date v) {
    java.sql.Date v0 = (java.sql.Date)v;
    putLongToBuffer (b, v0.getTime());
  }
  
  
  @Override
  public void setStatementFromValue (IPreparedStatement stmt, Date value) {
    stmt.setDate(value);
  }


  @Override
  public Date getResultValue (IResultSet resultSet) {
    return resultSet.getDate();
  }
  
}
