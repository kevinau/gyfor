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

import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.IType;
import org.gyfor.util.SimpleBuffer;

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

  
  public int getIntFromBuffer (SimpleBuffer b) {
    int v = b.next();
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Integer.MAX_VALUE;
    return v;
  }

  
  public void putIntToBuffer (SimpleBuffer b, int v) {
    b.ensureCapacity(Integer.BYTES);
    
    // Reverse the sign bit to allow byte sorting
    v ^= ~Integer.MAX_VALUE;
    b.append((v >>> 24) & 0xff);
    b.append((v >>> 16) & 0xff);
    b.append((v >>> 8) & 0xff);
    b.append(v & 0xff);
  }


  public short getShortFromBuffer (SimpleBuffer b) {
    int v = b.next();
    v = (v << 8) + (b.next() & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Short.MAX_VALUE;
    return (short)v;
  }

  
  public void putShortToBuffer (SimpleBuffer b, short v) {
    b.ensureCapacity(Short.BYTES);
    
    // Reverse the sign bit to allow byte sorting
    v ^= ~Short.MAX_VALUE;
    b.append((v >>> 8) & 0xff);
    b.append(v & 0xff);
  }
  

  public long getLongFromBuffer (SimpleBuffer b) {
    long v = b.next();
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Long.MAX_VALUE;
    return v;
  }

  
  public void putLongToBuffer (SimpleBuffer b, long v) {
   b.ensureCapacity(Long.BYTES);
    
    // Reverse the sign bit to allow byte sorting
    v ^= ~Long.MAX_VALUE;
    b.append((byte)((v >>> 56) & 0xff));
    b.append((byte)((v >>> 48) & 0xff));
    b.append((byte)((v >>> 40) & 0xff));
    b.append((byte)((v >>> 32) & 0xff));
    b.append((byte)((v >>> 24) & 0xff));
    b.append((byte)((v >>> 16) & 0xff));
    b.append((byte)((v >>> 8) & 0xff));
    b.append((byte)(v & 0xff));
  }
 
  
  public int getUTF8FromBuffer (SimpleBuffer b) {
    int v = b.next() & 0xff;
    if ((v & 0b1000_0000) == 0) {
      return v;
    } else if ((v & 0b1110_0000) == 0b1100_0000) {
      int v2 = b.next() & 0x3f;
      return ((v & 0x1f) << 6) | v2;
    } else if ((v & 0b1111_0000) == 0b1110_0000) {
      int v2 = b.next() & 0x3f;
      int v3 = b.next() & 0x3f;
      return ((v & 0xf) << 12) | (v2 << 6) | v3;
    } else {
      int v2 = b.next() & 0x3f;
      int v3 = b.next() & 0x3f;
      int v4 = b.next() & 0x3f;
      return ((v & 0x7) << 18) | (v2 << 12) | (v3 << 6) | v4;
    }    
  }
  
  
  public void putUTF8ToBuffer (SimpleBuffer b, int v) {
    b.ensureCapacity(4);
    
    if (v <= 0x7f) {
      b.append(v & 0xff);
    } else if (v <= 0x7ff) {
      b.append(0b1100_0000 | (v >>> 6) & 0x1f);
      b.append(0b1000_0000 | (v & 0x3f));
    } else if (v <= 0xffff) {
      b.append(0b1110_0000 | (v >>> 12) & 0x0f);
      b.append(0b1000_0000 | (v >>> 6) & 0x3f);
      b.append(0b1000_0000 | (v & 0x3f));
    } else {
      b.append(0b1111_0000 | (v >>> 18) & 0x7);
      b.append(0b1000_0000 | (v >>> 12) & 0x3f);
      b.append(0b1000_0000 | (v >>> 6) & 0x3f);
      b.append(0b1000_0000 | (v & 0x3f));
    }
  }

}
