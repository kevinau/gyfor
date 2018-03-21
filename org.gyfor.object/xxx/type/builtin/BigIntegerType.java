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
import java.math.BigInteger;

import org.gyfor.object.UserEntryException;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


public class BigIntegerType extends IntegerBasedType<BigInteger> {
  
  private static final BigInteger DEFAULT_MAX = new BigInteger("999999999999");
  private static final BigInteger DEFAULT_MIN = new BigInteger("-99999999999");
  
  public BigIntegerType () {
    super (DEFAULT_MIN, DEFAULT_MAX);
  }


  public BigIntegerType (BigInteger min, BigInteger max) {
    super (min, max);
  }


  @Override
  public BigInteger createFromString(String source) throws UserEntryException {
    validateIntegerSource(source);
    return new BigInteger(source);
  }


  @Override
  protected long longValue (BigInteger value) {
    return value.longValue();
  }
  
  
  @Override
  public BigInteger newInstance(String source) {
    return new BigInteger(source);
  }


  @Override
  public BigInteger primalValue() {
    return BigInteger.ZERO;
  }


  @Override
  protected void validate (BigInteger value) throws UserEntryException {
    checkWithinRange(value.longValue());
  }

  
  @Override
  public BigInteger getFromBuffer (SimpleBuffer b) {
    String s = b.nextNulTerminatedString();
    return new BigInteger(s);
  }


  @Override
  public void putToBuffer (SimpleBuffer b, BigInteger v) {
    BigInteger v0 = (BigInteger)v;
    b.appendNulTerminatedString(v0.toString());
  }


  @Override
  public int getBufferSize () {
    return BUFFER_NUL_TERMINATED;
  }
  
  
  @Override
  public String getSQLType() {
    return "DECIMAL(" + getMaxDigits() + ")";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, BigInteger value) {
    stmt.setBigDecimal(new BigDecimal(value));
  }


  @Override
  public BigInteger getResultValue(IResultSet resultSet) {
    return resultSet.getBigDecimal().toBigInteger();
  }

}
