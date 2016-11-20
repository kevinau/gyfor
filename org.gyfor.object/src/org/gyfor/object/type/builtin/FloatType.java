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

import org.gyfor.object.NumberSign;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.DecimalBasedType;


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
  public String getSQLType() {
    return "REAL";
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, Float value) throws SQLException {
    stmt.setFloat(sqlIndex, value);
  }


  @Override
  public Float getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    return resultSet.getFloat(sqlIndex);
  }

}
