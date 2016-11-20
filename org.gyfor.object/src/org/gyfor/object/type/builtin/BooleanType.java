/*******************************************************************************
ow * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
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

import org.gyfor.object.type.IType;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.Type;

public class BooleanType extends Type<Boolean> implements IType<Boolean> {
  
  private static final String REQUIRED_MESSAGE = "must be checked (Yes/1) or un-checked (No/0)";
  
  private final String checkedLabel;
  
  
  public BooleanType (String checkedLabel) {
    this.checkedLabel = checkedLabel;
  }
  
  
  public BooleanType () {
    this ("");
  }
  
  
  @Override
  public Boolean primalValue() {
    return Boolean.FALSE;
  }


  @Override
  public JsonType getJsonType () {
    return JsonType.BOOLEAN;
  }
  
  
  @Override
  public String getRequiredMessage() {
    return REQUIRED_MESSAGE;
  }


  @Override
  public String toEntryString(Boolean value, Boolean fillValue) {
    if (value == null) {
      return "";
    }
    boolean v = (Boolean) value;
    return v ? "Y" : "N";
  }

  @Override
  public String toValueString(Boolean value) {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    boolean v = (Boolean) value;
    return v ? "true" : "false";
  }

  /**
   * Return the description of a value. Can return <code>null</code> if it does
   * not have a description that is distinct from its entry representation. If a
   * non-null value is returned, it should not duplicate the entry
   * representation.
   */
  @Override
  public String toDescriptionString(Boolean value) {
    boolean v = (Boolean) value;
    return v ? "Checked" : "Un-checked";
  }

  
  @Override
  public Boolean createFromString (String source) throws UserEntryException {
    Boolean value;
    char c = source.charAt(0);
    switch (c) {
    case 'Y':
    case 'y':
    case '1':
      value = Boolean.TRUE;
      break;
    case 'N':
    case 'n':
    case '0':
      value = Boolean.FALSE;
      break;
    default:
      throw new UserEntryException("not checked (Yes/1) or un-checked (No/0)");
    }
    return value;
  }


  @Override
  protected void validate (Boolean value) throws UserEntryException {
    // No additional validation required
  }

  
  public String getCheckedLabel () {
    return checkedLabel;
  }


  @Override
  public Boolean newInstance(String source) {
    try {
      return createFromString(source);
    } catch (UserEntryException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }


  @Override
  public int getFieldSize() {
    return 1;
  }


  @Override
  public String getSQLType() {
    return "BOOLEAN";
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, Boolean value) throws SQLException {
    stmt.setBoolean(sqlIndex, (Boolean)value);
  }


  @Override
  public Boolean getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    return resultSet.getBoolean(sqlIndex);
  }
  
}
