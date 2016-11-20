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

import org.gyfor.object.type.IType;
import org.gyfor.object.UserEntryException;

public abstract class Type<T> implements IType<T> {

  private boolean primitive = false;
  private boolean nullable = false;
  
  public void setPrimitive (boolean primitive) {
    this.primitive = primitive;
  }
  
  @Override
  public boolean isPrimitive () {
    return primitive;
  }
  
  
  public void setNullable(boolean nullable) {
    this.nullable = nullable;
  }
  
  @Override
  public boolean isNullable () {
    return nullable;
  }

  @Override
  public abstract T createFromString (String source) throws UserEntryException;
  
  
  @Override
  public T createFromString (T fillValue, boolean nullable, boolean creating, String source) throws UserEntryException {
    source = source.trim();
    if (source.length() == 0) {
      if (nullable) {
        return null;
      } else {
        throw new UserEntryException(getRequiredMessage(), UserEntryException.Type.REQUIRED);
      }
    }
    return createFromString(fillValue, source);
  }
    
  
  protected T createFromString (T fillValue, String source) throws UserEntryException {
    return createFromString(source);
  }
  
  
  @Override
  public abstract String getRequiredMessage (); 
  
  
  @Override 
  public abstract T primalValue ();
  
  
  @Override
  public abstract T newInstance(String source);
    
  
  @Override
  public String toDescriptionString(T value) {
    return null;
  }
  
  
  @Override
  public String toEntryString(T value, T fillValue) {
    if (value == null) {
      return "";
    }
    return value.toString();
  }
  
  
  @Override
  public String toValueString(T value) {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    return value.toString();
  }
  
  
  protected abstract void validate(T value) throws UserEntryException;

  
  @Override
  public void validate(T value, boolean nullable) throws UserEntryException {
    if (value == null) {
      if (nullable) {
        return;
      } else {
        throw new UserEntryException(getRequiredMessage());
      }
    }
    validate(value);
  }

}
