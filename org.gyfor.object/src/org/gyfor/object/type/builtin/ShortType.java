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

public class ShortType extends IntegerBasedType<Short> {
  
  public ShortType () {
    super (Short.MIN_VALUE, Short.MAX_VALUE);
  }


  public ShortType (short min, short max) {
    super (min, max);
  }


  @Override
  public Short createFromString(String source) throws UserEntryException {
    validateIntegerSource(source);
    return Short.parseShort(source);
  }


  @Override
  protected long longValue (Short value) {
    return value;
  }
  
  
  @Override
  public Short newInstance(String source) {
    return Short.parseShort(source);
  }


  @Override
  public Short primalValue() {
    return 0;
  }


  @Override
  protected void validate (Short value) throws UserEntryException {
    checkWithinRange(value);
  }


  @Override
  public Short getFromBuffer (SimpleBuffer b) {
    return getShortFromBuffer(b);
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, Short v) {
    putShortToBuffer(b, (short)v);
  }
  
  
  @Override
  public int getBufferSize () {
    return Short.BYTES;
  }
  
  
  @Override
  public String getSQLType() {
    return "SMALLINT";
  }

  
  @Override
  public void setStatementFromValue(IPreparedStatement stmt, Short value) {
    stmt.setShort(value);
  }


  @Override
  public Short getResultValue(IResultSet resultSet) {
    return resultSet.getShort();
  }

}
