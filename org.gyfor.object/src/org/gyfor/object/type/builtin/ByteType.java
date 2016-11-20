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

import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.IntegerBasedType;

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
  public String getSQLType() {
    return "SMALLINT";
  }

  
  @Override
  protected void validate (Byte value) throws UserEntryException {
    checkWithinRange(value);
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, Byte value) throws SQLException {
    stmt.setShort(sqlIndex, value.shortValue());
  }


  @Override
  public Byte getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    return (byte)resultSet.getShort(sqlIndex);
  }

}
