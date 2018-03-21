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

public class IntegerType extends IntegerBasedType<Integer> {
    
  public IntegerType () {
    super (Integer.MIN_VALUE, Integer.MAX_VALUE);
  }


  public IntegerType (int min, int max) {
    super (min, max);
  }


  @Override
  public Integer createFromString(String source) throws UserEntryException {
    validateIntegerSource(source);
    return Integer.parseInt(source);
  }



  @Override
  protected long longValue (Integer value) {
    return value;
  }
  
  
  @Override
  public Integer newInstance(String source) {
    return Integer.parseInt(source);
  }


  @Override
  public Integer primalValue() {
    return 0;
  }


  @Override
  protected void validate (Integer value) throws UserEntryException {
    checkWithinRange(value);
  }


  @Override
  public Integer getFromBuffer (SimpleBuffer b) {
    return getIntFromBuffer(b);
  }

  
  @Override
  public void putToBuffer (SimpleBuffer b, Integer v) {
    putIntToBuffer(b, (Integer)v);
  }

  
  @Override
  public int getBufferSize () {
    return Integer.BYTES;
  }
  
  
  @Override
  public String getSQLType() {
    return "INTEGER";
  }


  @Override
  public void setStatementFromValue(IPreparedStatement stmt, Integer value) {
    stmt.setInt(value);
  }


  @Override
  public Integer getResultValue(IResultSet resultSet) {
    return resultSet.getInt();
  }

}
