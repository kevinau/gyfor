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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.DateBasedType;


public class LocalDateType extends DateBasedType<LocalDate> {
  
  public LocalDateType () {
  }


  @Override
  public LocalDate primalValue() {
    return LocalDate.now();
  }


  @Override
  public LocalDate today() {
    return LocalDate.now();
  }
  
  
  @Override
  protected void validate(LocalDate date) throws UserEntryException {
    // Nothing more to validate
  }


  @Override
  protected LocalDate createFromYearMonthDay(int year, int month, int day) {
    return LocalDate.of(year, month, day);
  }


  @Override
  protected int[] splitDate(LocalDate date) {
    int[] components = new int[3];
    components[0] = date.getYear();
    components[1] = date.getMonthValue();
    components[2] = date.getDayOfMonth();
    return components;
  }


  @Override
  public String toValueString (LocalDate value) {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    return value.toString();
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, LocalDate value) throws SQLException {
    stmt.setDate(sqlIndex, new java.sql.Date(value.toEpochDay()), tzCal);
  }


  @Override
  public LocalDate getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    java.util.Date d = (java.util.Date)resultSet.getDate(sqlIndex, tzCal);
    return LocalDate.ofEpochDay(d.getTime());
  }
  
}

