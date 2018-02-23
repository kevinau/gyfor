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

import java.util.Collections;
import java.util.List;
import org.eclipse.jdt.annotation.NonNull;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.IType;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;
import org.gyfor.value.ICode;


public class CodeType<T extends ICode> implements IType<T> {

  private final @NonNull Class<T> codeClass;
  private @NonNull List<T> values;

  public CodeType(@NonNull Class<T> codeClass) {
    this.codeClass = codeClass;
    this.values = Collections.emptyList();
  }

  public CodeType(@NonNull Class<T> codeClass, @NonNull List<T> values) {
    this.codeClass = codeClass;
    this.values = values;
  }

  public void setValues(@NonNull List<T> values) {
    this.values = values;
  }
  
  // public IEntryControl2 createEntryControl (Composite parent, boolean
  // allowEmpty, boolean primaryKey) {
  // CodeEntryControl control = new CodeEntryControl(parent, valueFactory, null,
  // allowEmpty, primaryKey);
  // return control;
  // }
  //
  //
  // public IViewControl createViewControl (Composite parent) {
  // return new EnumViewControl(parent, valueFactory);
  // }

  @Override
  public T createFromString(String source) throws UserEntryException {
    for (T cv : values) {
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

    for (T cv : values) {
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
  public String toEntryString(T value, T fillValue) {
    if (value == null) {
      return "";
    }
    return value.getCode();
  }

  @Override
  public String toValueString(T value) {
    if (value == null) {
      throw new IllegalArgumentException("value must not be null");
    }
    return value.getCode();
  }

  @Override
  public String toDescriptionString(T value) {
    if (value == null) {
      return "";
    }
    return value.getDescription();
  }

  @Override
  public int getFieldSize() {
    int n = 0;
    for (ICode cv : values) {
      String code = cv.getCode();
      n = Integer.max(n, code.length());
    }
    return n;
  }

  @Override
  public T primalValue() {
    return values.get(0);
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
    for (T v : values) {
      if (v.equals(value)) {
        return;
      }
    }
    throw new UserEntryException("'" + value.getCode() + "' is an illegal value");
  }

  @Override
  public T newInstance(String code) {
    for (T v : values) {
      if (v.getCode().equals(code)) {
        return v;
      }
    }
    throw new RuntimeException("Illegal value: " + code);
  }

  @Override
  public String getRequiredMessage() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("must be one of the values: ");
    int i = 0;
    int n = values.size() - 1;
    for (T cv : values) {
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
  public T getFromBuffer(SimpleBuffer b) {
    String s = b.nextNulTerminatedString();
    try {
      return createFromString(s);
    } catch (UserEntryException ex) {
      throw new RuntimeException("Illegal value: " + s);
    }
  }

  @Override
  public void putToBuffer(SimpleBuffer b, T v) {
    b.appendNulTerminatedString(v.toString());
  }

  @Override
  public int getBufferSize() {
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
