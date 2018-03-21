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


import java.time.LocalDate;

import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


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
  public LocalDate getFromBuffer (SimpleBuffer b) {
    long n = getLongFromBuffer(b);
    return LocalDate.ofEpochDay(n);
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, LocalDate v) {
    LocalDate v0 = (LocalDate)v;
    putLongToBuffer (b, v0.toEpochDay());
  }
  
  
  @Override
  public void setStatementFromValue(IPreparedStatement stmt, LocalDate value) {
    stmt.setLocalDate(value);
  }


  @Override
  public LocalDate getResultValue(IResultSet resultSet) {
    return resultSet.getLocalDate();
  }
  
}

