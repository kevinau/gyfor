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

import org.gyfor.object.type.IType;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.Type;
import org.gyfor.object.value.ICodeValue;


public class EnumType<E extends Enum<E>> extends Type<E> implements IType<E> {

  private final Class<E> enumClass;
  private final boolean isCodeValues;
  
  public EnumType (Class<E> enumClass) {
    Object[] values = enumClass.getEnumConstants();
    if (values == null) {
      throw new IllegalArgumentException("Not an enum class");
    }
    if (values.length == 0) {
      throw new IllegalArgumentException("No values in enum class");
    }
    this.isCodeValues = (values[0] instanceof ICodeValue);
    this.enumClass = enumClass;
  }
  
  
  public boolean isCodeValues () {
    return isCodeValues;
  }
  
  
  public String[] getCodes () {
    Object[] values = enumClass.getEnumConstants();
    String[] codes = new String[values.length];
    if (ICodeValue.class.isAssignableFrom(enumClass)) {
      for (int i = 0; i < values.length; i++) {
        ICodeValue ev = (ICodeValue)values[i];
        codes[i] = ev.getCode();
      }
    } else {
      for (int i = 0; i < values.length; i++) {
        String x = values[i].toString();
        x = x.toLowerCase().replace('_', ' ');
        codes[i] = Character.toUpperCase(x.charAt(0)) + x.substring(1);
      }
    }
    return codes;
  }
  

  public Object[] getEnumValues () {
    try {
      Object[] enumValues = enumClass.getEnumConstants();
      return enumValues;
    } catch (SecurityException ex) {
      throw new RuntimeException(ex);
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public String[] getDescriptions () {
    Object[] values = enumClass.getEnumConstants();
    String[] descs = new String[values.length];
    if (ICodeValue.class.isAssignableFrom(enumClass)) {
      for (int i = 0; i < values.length; i++) {
        ICodeValue ev = (ICodeValue)values[i];
        descs[i] = ev.getDescription();
      }
    }
    return descs;
  }
  
  
  public int getViewSize () {
    int n = 0;
    Object[] values = enumClass.getEnumConstants();
    if (ICodeValue.class.isAssignableFrom(enumClass)) {
      for (int i = 0; i < values.length; i++) {
        ICodeValue ev = (ICodeValue)values[i];
        int n1 = ev.getCode().length();
        if (n1 > n) {
          n = n1;
        }
      }
    } else {
      for (Object value : values) {
        int n1 = value.toString().length();
        if (n1 > n) {
          n = n1;
        }
      }
    }
    return n;
  }
  
  
  
  public boolean isEmpty () {
    return false;
  }
  
  
  public E create (Object source) {
    if (source instanceof String) {
      E[] values = enumClass.getEnumConstants();
      if (ICodeValue.class.isAssignableFrom(enumClass)) {
        for (int i = 0; i < values.length; i++) {
          ICodeValue cv = (ICodeValue)values[i];
          if (cv.getCode().equals(source)) {
            return values[i];
          } else if (Integer.toString(i).equals(source)) {
            return values[i];
          }
        }
        throw new IllegalArgumentException((String)source);
      } else {
        try {
          String s = (String)source;
          int i = Integer.parseInt(s);
          if (i >= 0 && i < values.length){
            return values[i];
          } else {
            throw new IllegalArgumentException(s);
          }
        } catch (NumberFormatException ex) {
          throw new IllegalArgumentException((String)source);
        }
//        for (E value : values) {
//          if (value.toString().equals(source)) {
//            return value;
//          }
//        }
      }
    } else if (source instanceof Integer) {
      E[] values = enumClass.getEnumConstants();
      int i = ((Integer)source).intValue();
      if (i >= 0 && i < values.length) {
        return values[i];
      } else {
        throw new IllegalArgumentException((String)source);
      }
    }
    throw new IllegalArgumentException(source.getClass().getName());
  }
  
     
  @Override
  public int getFieldSize () {
    Object[] values = enumClass.getEnumConstants();

    int n = 0;
    if (ICodeValue.class.isAssignableFrom(enumClass)) {
      for (int i = 0; i < values.length; i++) {
        ICodeValue ev = (ICodeValue)values[i];
        String code = ev.getCode();
        n = Integer.max(n, code.length());
      }
    } else {
      for (int i = 0; i < values.length; i++) {
        String x = values[i].toString();
        n = Integer.max(n,  x.length());
      }
    }
    return n;
  }
  

  @Override
  public E createFromString (String source) throws UserEntryException {
    E[] values = enumClass.getEnumConstants();
    if (ICodeValue.class.isAssignableFrom(enumClass)) {
      for (int i = 0; i < values.length; i++) {
        ICodeValue cv = (ICodeValue)values[i];
        if (cv.getCode().equals(source)) {
          validate (values[i]);
          return values[i];
        }
      }
      boolean isIncomplete = false;
      for (int i = 0; i < values.length; i++) {
        ICodeValue cv = (ICodeValue)values[i];
        if (cv.getCode().startsWith(source)) {
          isIncomplete = true;
          break;
        }
      }
      throw new UserEntryException(getErrorMessage(values), isIncomplete);
    } else {
      try {
        int i = Integer.parseInt(source);
        if (i >= 0 && i < values.length){
          validate (values[i]);
          return values[i];
        } else {
          throw new UserEntryException(getErrorMessage(values));
        }
      } catch (NumberFormatException ex) {
        throw new UserEntryException(getErrorMessage(values));
      }
    }
  }

  
  @Override
  protected void validate (E value) throws UserEntryException {
    // No more validation to do
  }

  
  protected String getErrorMessage (ICodeValue[] values) {
    StringBuilder msg = new StringBuilder();
    if (values.length == 0) {
      msg.append("no values to select from");
    } else {
      for (int i = 0; i < values.length; i++) {
        if (i == 0) {
          msg.append("one of ");
        } else if (i + 1 < values.length) {
          msg.append(", ");
        } else {
          msg.append(" or ");
        }
        msg.append(values[i].getCode());
      }
      msg.append(" required");
    }
    return msg.toString();
  }
  

  protected String getErrorMessage (E[] values) {
    StringBuilder msg = new StringBuilder();
    if (values.length == 0) {
      msg.append("no values to select from");
    } else {
      String[] codes = getCodes();
      for (int i = 0; i < values.length; i++) {
        if (i == 0) {
          msg.append("one of ");
        } else if (i + 1 < values.length) {
          msg.append(", ");
        } else {
          msg.append(" or ");
        }
//        if (values[i] instanceof ICodeValue) {
//          msg.append(((ICodeValue)values[i]).getCode());
//        } else {
//          msg.append(values[i].toString());
//        }
        msg.append(codes[i]);
      }
      msg.append(" required");
    }
    return msg.toString();
  }
  

  @Override
  public String toEntryString (E value, E fillValue) {
    if (value == null) {
      return "";
    }
    if (value instanceof ICodeValue) {
      ICodeValue cv = (ICodeValue)value;
      return cv.getCode();
    } else {
      return Integer.toString(((Enum<?>)value).ordinal());
    }
  }
  
  
  @Override
  public JsonType getJsonType () {
    return JsonType.PLAIN_STRING;
  }
  
  
  @Override
  public E primalValue () {
    E[] values = enumClass.getEnumConstants();
    return values[0];
  }


  @Override
  public String getRequiredMessage() {
    return "a value is required";
  }


  @Override
  public E newInstance(String source) {
    try {
      return createFromString(source);
    } catch (UserEntryException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }


  @Override
  public String getSQLType() {
    return "SMALLINT";
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, E value) throws SQLException {
    stmt.setShort(sqlIndex, (short)value.ordinal());
  }


  @Override
  public E getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    short i = resultSet.getShort(sqlIndex);
    E[] values = enumClass.getEnumConstants();
    if (i >= 0 && i < values.length) {
      return values[i];
    } else {
      throw new IllegalArgumentException("Ordinal value: " + i);
    }
  }


}
