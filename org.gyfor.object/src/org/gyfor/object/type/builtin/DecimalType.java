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

import java.math.BigDecimal;

import org.gyfor.math.Decimal;
import org.gyfor.object.NumberSign;
import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


public class DecimalType extends DecimalBasedType<Decimal> {

  public DecimalType () {
    super (10, 0);
  }
  
  
  public DecimalType (NumberSign sign, int precision, int scale) {
    super (sign, precision, scale);
  }

  
  public DecimalType (NumberSign sign, int precision) {
    super (sign, precision);
  }

  
  public DecimalType (int precision, int scale) {
    super (precision, scale);
  }

  
  public DecimalType (int precision) {
    super (precision);
  }

  
  @Override
  public Decimal primalValue() {
    return Decimal.ZERO;
  }


  @Override
  public JsonType getJsonType () {
    return JsonType.PLAIN_STRING;
  }
  
  
  @Override
  public Decimal newInstance(String source) {
    return new Decimal(source);
  }


  @Override
  public Decimal createFromString(String source) throws UserEntryException {
    validateDecimalSource(source);
    return new Decimal(source);
  }


  @Override
  protected void validate(Decimal value) throws UserEntryException {
    validatePrecision(value.longValue());
    validateDecimals(value.trim().getScale());
  }


  @Override
  public Decimal getFromBuffer (SimpleBuffer b) {
    int s1 = getIntFromBuffer(b);
    long v1 = getLongFromBuffer(b);
    if (s1 < 0) {
      s1 = -s1;
    }
    s1 = 64 - s1;
    Decimal v = new Decimal(v1, s1).trim();
    return v;
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, Decimal v) {
    Decimal normal = v.normalize();
    long v1 = normal.getRawLong();
    // The scale here is positive, with a higher number representing a higher value
    int s1 = 64 - normal.getScale();

    if (v1 < 0) {
      s1 = -s1;
    }
    putIntToBuffer (b, s1);
    putLongToBuffer (b, v1);
  }
  
  
  @Override
  public int getBufferSize () {
    return Integer.BYTES + Long.BYTES;
  }
  
  
  @Override
  public void setStatementFromValue(IPreparedStatement stmt,Decimal value) {
    stmt.setBigDecimal(new BigDecimal(value.toString()));
  }


  @Override
  public Decimal getResultValue(IResultSet resultSet) {
    BigDecimal bd = resultSet.getBigDecimal();
    return new Decimal(bd.toString());
  }

}
