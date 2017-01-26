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
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;

public class ByteType extends IntegerBasedType<Byte> {
  
  public ByteType () {
    super (Byte.MIN_VALUE, Byte.MAX_VALUE);
  }


  public ByteType (byte min, byte max) {
    super (min, max);
  }


  @Override
  public Byte createFromString(String source) throws UserEntryException {
    validateIntegerSource(source);
    return Byte.parseByte(source);
  }


  @Override
  protected long longValue (Byte value) {
    return value;
  }
  
  
  @Override
  public Byte newInstance(String source) {
    return Byte.parseByte(source);
  }


  @Override
  public Byte primalValue() {
    return 0;
  }

  
  @Override
  public Byte getFromBuffer (SimpleBuffer b) {
    int v = b.next();
    // Reverse the sign bit that was stored
    v ^= ~Byte.MAX_VALUE;
    return (byte)v;
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, Byte v) {
    byte v0 = (byte)v;
    // Reverse the sign bit to allow byte sorting
    v0 ^= ~Byte.MAX_VALUE;
    b.append(v0);
  }
  
  
  @Override
  public int getBufferSize () {
    return Byte.BYTES;
  }
  
  
  @Override
  public String getSQLType() {
    return "SMALLINT";
  }

  
  @Override
  protected void validate (Byte value) throws UserEntryException {
    checkWithinRange(value);
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, int sqlIndex, Byte value) {
    stmt.setShort(sqlIndex, value.shortValue());
  }


  @Override
  public Byte getResultValue(IResultSet resultSet, int sqlIndex) {
    return (byte)resultSet.getShort(sqlIndex);
  }

}
