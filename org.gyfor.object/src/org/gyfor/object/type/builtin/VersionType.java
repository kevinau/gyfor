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


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.value.VersionTime;


public class VersionType extends StringBasedType<VersionTime> {

  private static final String REQUIRED_MESSAGE = "a date/time is required";
  
  private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  
  public VersionType () {
  	super (10 + 1 + 8);
  }
  
  
  @Override
  public String getRequiredMessage () {
    return REQUIRED_MESSAGE;
  }
  
  
  @Override
  public VersionTime createFromString (String source) throws UserEntryException {
    try {
      long x = format.parse(source).getTime();
      Timestamp t = new Timestamp(x);
      return new VersionTime(t);
    } catch (ParseException ex) {
      throw new UserEntryException("invalid date/time");
    }
  }
  
  
  @Override
  public VersionTime primalValue () {
    return new VersionTime(new Timestamp(0L));
  }
  
  
  @Override
  public VersionTime newInstance (String source) {
    try {
      long x = format.parse(source).getTime();
      Timestamp t = new Timestamp(x);
      return new VersionTime(t);
    } catch (ParseException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  protected void validate(VersionTime value) throws UserEntryException {
    // Nothing more to do
  }


  @Override
  public String getSQLType() {
    return "TIMESTAMP";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, VersionTime value) {
    stmt.setTimestamp(new Timestamp(value.fileTimeValue().toMillis()));
  }


  @Override
  public VersionTime getResultValue(IResultSet resultSet) {
    Timestamp t = resultSet.getTimestamp();
    return new VersionTime(t);
  }

}

