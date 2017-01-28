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


import java.util.List;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.IType;
import org.gyfor.object.value.ICodeValue;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


public abstract class CodeBasedType<T extends ICodeValue> implements IType<T> {

  private final String shortName;
  private final String longName;
  private List<T> valueList;
  
  public CodeBasedType () {
    this (null, null);
  }
  
  
  public CodeBasedType (List<T> valueList) {
    this (valueList, null, null);
  }
  
  
  public CodeBasedType (String shortName, String longName) {
    this (null, shortName, longName);
  }
  
  
  public CodeBasedType (List<T> valueList, String shortName, String longName) {
    this.valueList = valueList;
    this.shortName = shortName;
    this.longName = longName;
  }
  

  protected void setValueList (List<T> valueList) {
    this.valueList = valueList;
  }
  
  
  /**
   * Allows late population of the value list.  This method will be called before 
   * the valueList is required.  It should be overridden by those classes that
   * do not supply a value list in the constructor.  The valueList argument is to
   * allow the calling program to determine if any update of the value list is 
   * required.
   */
  protected void lazyValueListUpdate (List<T> valueList) {
  }
  
  
  public List<T> getValueList () {
    lazyValueListUpdate(valueList);
    return valueList;
  }
  
  
//  public IEntryControl2 createEntryControl (Composite parent, boolean allowEmpty, boolean primaryKey) {
//    CodeEntryControl control = new CodeEntryControl(parent, valueFactory, null, allowEmpty, primaryKey);
//    return control;
//  }
//  
//  
//  public IViewControl createViewControl (Composite parent) {
//    return new EnumViewControl(parent, valueFactory);
//  }


  @Override
  public T createFromString(String source) throws UserEntryException {
    lazyValueListUpdate(valueList);
    for (T cv : valueList) {
      String code = cv.getCode();
      if (code.equals(source)) {
        return cv;
      }
    }
    throw new UserEntryException("not one of the allowed values");
  }
  
  
  @Override
  public T createFromString(T fillValue, boolean nullable, boolean creating, String source) throws UserEntryException {
    if (source.length() == 0) {
      if (nullable) {
        return null;
      } else {
        throw new UserEntryException(getRequiredMessage(), UserEntryException.Type.REQUIRED);
      }
    }

    int incompl = 0;
    String incomplx = null;
    
    lazyValueListUpdate(valueList);
    for (T cv : valueList) {
      String code = cv.getCode();
      if (code.equals(source)) {
        return cv;
      }
      if (code.startsWith(source)) {
        // This is a possible incomplete
        if (incompl == 0) {
          incomplx = code.substring(source.length());
        }
        incompl++;
      }
    }
    
    if (creating) {
      // TODO need to sort this out
    }
    switch (incompl) {
    case 0 :
      throw new UserEntryException("not one of the allowed values");
    case 1 :
      throw new UserEntryException(getRequiredMessage(), UserEntryException.Type.INCOMPLETE, incomplx);
    default :
      throw new UserEntryException(getRequiredMessage(), UserEntryException.Type.INCOMPLETE);
    }
  }
  
  
  @Override
  public String toEntryString (T value, T fillValue) {
    if (value == null) {
      return "";
    }
    return value.getCode();
  }
  
  
  @Override
  public String toValueString (T value) {
    if (value == null) {
      throw new IllegalArgumentException("value must not be null");
    }
    return value.getCode();
  }
  
  
  @Override
  public String toDescriptionString (T value) {
    if (value == null) {
      return "";
    }
    return value.getDescription();
  }
  
  
  public String getShortName () {
    return shortName;
  }
  
  
  public String getLongName () {
    return longName;
  }
  
  
  @Override
  public int getFieldSize () {
    lazyValueListUpdate(valueList);
    int n = 0;
    for (ICodeValue cv : valueList) {
      String code = cv.getCode();
      n = Integer.max(n, code.length());
    }
    return n;
  }
  
  
  @Override
  public JsonType getJsonType () {
    return JsonType.PLAIN_STRING;
  }
  
  
  @Override
  public T primalValue() {
    lazyValueListUpdate(valueList);
    return valueList.get(0);
  }

  
  @Override
  public void validate(T value, boolean nullable) throws UserEntryException {
    if (value == null) {
      if (isNullable()) {
        return;
      } else {
        throw new UserEntryException("missing value");
      }
    }
    lazyValueListUpdate(valueList);
    for (T v : valueList) {
      if (v.equals(value)) {
        return;
      }
    }
    throw new UserEntryException("'" + value.getCode() + "' is an illegal value");
  }
  
  
  @Override
  public T newInstance (String source) {
    lazyValueListUpdate(valueList);
    for (T v : valueList) {
      if (v.equals(source)) {
        return v;
      }
    }
    throw new RuntimeException("Illegal value: " + source);
  }
  
  
  @Override
  public String getRequiredMessage() {
    lazyValueListUpdate(valueList);
    StringBuilder buffer = new StringBuilder();
    buffer.append("must be one of the values: ");
    int i = 0;
    int n = valueList.size() - 1;
    for (T cv : valueList) {
      if (i > 0) {
        if (i == n) {
          buffer.append(" or ");
        } else {
          buffer.append(", ");
        }
      }
      buffer.append(cv.getCode());
    }
    return buffer.toString();
  }

  
  @Override
  public T getFromBuffer (SimpleBuffer b) {
    String s = b.nextNulTerminatedString();
    try {
      return createFromString(s);
    } catch (UserEntryException ex) {
      throw new RuntimeException("Illegal value: " + s);
    }
  }
  
  
  @Override
  public void putToBuffer (SimpleBuffer b, T v) {
    b.appendNulTerminatedString(v.toString());
  }
  
  
  @Override
  public int getBufferSize () {
    return BUFFER_NUL_TERMINATED;
  }
  
  
  @Override
  public String getSQLType() {
    return "VARCHAR(" + getFieldSize() + ")";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, T value) {
    stmt.setString(value.getCode());
  }


  @Override
  public T getResultValue(IResultSet resultSet) {
    String s = resultSet.getString();
    try {
      return createFromString(s);
    } catch (UserEntryException ex) {
      throw new RuntimeException("Illegal value: " + s);
    }
  }

}
