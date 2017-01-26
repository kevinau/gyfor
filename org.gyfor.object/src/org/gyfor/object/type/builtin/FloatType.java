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

import org.gyfor.object.NumberSign;
import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


public class FloatType extends DecimalBasedType<Float> {

  public FloatType () {
    super (8, 0);
  }

  
  public FloatType (int precision) {
    super (precision);
  }

  
  public FloatType (int precision, int decimals) {
    super (precision, decimals);
  }

  
  public FloatType (NumberSign sign, int precision) {
    super (sign, precision);
  }

  
  public FloatType (NumberSign sign, int precision, int decimals) {
    super (sign, precision, decimals);
  }


  @Override
  public Float createFromString(String source) throws UserEntryException {
    validateDecimalSource (source);
    return Float.parseFloat(source);
  }


  @Override
  public Float newInstance(String source) {
    return Float.parseFloat(source);
  }


  @Override
  public Float primalValue() {
    return 0F;
  }
  
  
  @Override
  protected void validate(Float value) throws UserEntryException {
    validatePrecision(value.longValue());
  }
 
  
  @Override
  public Float getFromBuffer(SimpleBuffer b) {
    int v = b.next() & 0xff;
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    // Reverse the sign bit that was stored 
    v ^= (v >> 31) & Integer.MAX_VALUE;
    return Float.intBitsToFloat(v);
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, Float v) {
    float v0 = (float)v;
    int v1 = Float.floatToRawIntBits(v0);
    // Reverse the sign bit to allow byte sorting negative floats have bits 0-30
    // inverted (because you want the opposite order to what the original
    // sign/magnitude representation would give you, whilst preserving the sign
    // bit.
    v1 ^= (v1 >> 31) & Integer.MAX_VALUE;
    b.append((v1 >>> 24) & 0xff);
    b.append((v1 >>> 16) & 0xff);
    b.append((v1 >>> 8) & 0xff);
    b.append(v1 & 0xff);
  }

  
  @Override
  public int getBufferSize () {
    return Float.BYTES;
  }
  
  
  @Override
  public String getSQLType() {
    return "REAL";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, int sqlIndex, Float value) {
    stmt.setFloat(sqlIndex, value);
  }


  @Override
  public Float getResultValue(IResultSet resultSet, int sqlIndex) {
    return resultSet.getFloat(sqlIndex);
  }

}
