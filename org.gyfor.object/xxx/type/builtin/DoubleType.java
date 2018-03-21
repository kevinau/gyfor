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


public class DoubleType extends DecimalBasedType<Double> {

  private static final Double ZERO = new Double(0);
  

  public DoubleType () {
    super (10, 0);
  }

  
  public DoubleType (int precision) {
    super (precision);
  }

  
  public DoubleType (int precision, int decimals) {
    super (precision, decimals);
  }

  
  public DoubleType (NumberSign sign, int precision) {
    super (sign, precision);
  }

  
  public DoubleType (NumberSign sign, int precision, int decimals) {
    super (sign, precision, decimals);
  }


  @Override
  public Double createFromString(String source) throws UserEntryException {
    validateDecimalSource (source);
    return Double.parseDouble(source);
  }


  @Override
  public Double newInstance(String source) {
    return Double.parseDouble(source);
  }


  @Override
  public Double primalValue() {
    return ZERO;
  }
  
  
  @Override
  protected void validate(Double value) throws UserEntryException {
    validatePrecision(value.longValue());
  }

  
  @Override
  public Double getFromBuffer (SimpleBuffer b) {
    long v = b.next() & 0xff;
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    v = (v << 8) + (b.next() & 0xff);
    // Reverse the sign bit that was stored 
    v ^= (v >> 63) & Long.MAX_VALUE;
    return Double.longBitsToDouble(v);
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, Double v) {
    double v0 = (double)v;
    long v1 = Double.doubleToRawLongBits(v0);
    // Reverse the sign bit to allow byte sorting negative doubles have bits 0-30
    // inverted (because you want the opposite order to what the original
    // sign/magnitude representation would give you, whilst preserving the sign
    // bit.
    v1 ^= (v1 >> 63) & Long.MAX_VALUE;
    b.append((byte)((v1 >>> 56) & 0xff));
    b.append((byte)((v1 >>> 48) & 0xff));
    b.append((byte)((v1 >>> 40) & 0xff));
    b.append((byte)((v1 >>> 32) & 0xff));
    b.append((byte)((v1 >>> 24) & 0xff));
    b.append((byte)((v1 >>> 16) & 0xff));
    b.append((byte)((v1 >>> 8) & 0xff));
    b.append((byte)(v1 & 0xff));
  }

  
  @Override
  public int getBufferSize () {
    return Double.BYTES;
  }
  
  
  @Override
  public String getSQLType() {
    return "DOUBLE";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, Double value) {
    stmt.setDouble(value);
  }


  @Override
  public Double getResultValue(IResultSet resultSet)  {
    return resultSet.getDouble();
  }

}
