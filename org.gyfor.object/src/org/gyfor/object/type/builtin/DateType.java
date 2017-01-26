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


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


public class DateType extends DateBasedType<Date> {
  
  public DateType () {
  }
  
  
  @Override
  public Date primalValue() {
    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, 0);
    calendar.set(Calendar.MONTH, 0);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    return calendar.getTime();
  }


  @Override
  public Date today() {
    return new Date();
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
    return calendar.getTime();
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
  public Date getFromBuffer (SimpleBuffer b) {
    long n = getLongFromBuffer(b);
    return new Date(n);
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, Date v) {
    Date v0 = (Date)v;
    putLongToBuffer (b, v0.getTime());
  }
  
  
  @Override
  public void setStatementFromValue (IPreparedStatement stmt, int sqlIndex, Date value) {
    stmt.setDate(sqlIndex, new java.sql.Date(value.getTime()));
  }


  @Override
  public Date getResultValue (IResultSet resultSet, int sqlIndex) {
    return resultSet.getDate(sqlIndex);
  }
 
}

