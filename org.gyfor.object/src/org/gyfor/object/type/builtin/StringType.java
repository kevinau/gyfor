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


import org.gyfor.object.TextCase;
import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;


public class StringType extends StringBasedType<String> {

  
  public StringType () {
    super ();
  }
  
  
  public StringType (int maxLength) {
    super (maxLength);
  }
  
  
  public StringType (int maxLength, TextCase allowedCase) {
    super (maxLength, allowedCase);
  }


  @Override
  protected String createFromString2(String source) throws UserEntryException {
    String value = newInstance(source);
    validate (value);
    return value;
  }


  @Override
  public String newInstance(String source) {
    return source;
  }


  @Override
  protected void validate(String value) throws UserEntryException {
  }
  
  
  @Override
  public String primalValue () {
    return "";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, int sqlIndex, String value) {
    stmt.setString(sqlIndex,  value);
  }


  @Override
  public String getResultValue(IResultSet resultSet, int sqlIndex) {
    return resultSet.getString(sqlIndex);
  }

}
