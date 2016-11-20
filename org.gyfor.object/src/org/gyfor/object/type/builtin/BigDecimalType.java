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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gyfor.object.NumberSign;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.DecimalBasedType;


public class BigDecimalType extends DecimalBasedType<BigDecimal> {

  public BigDecimalType () {
    super (10, 0);
  }

  
  public BigDecimalType (int precision) {
    super (precision);
  }

  
  public BigDecimalType (int precision, int decimals) {
    super (precision, decimals);
  }

  
  public BigDecimalType (NumberSign sign, int precision) {
    super (sign, precision);
  }

  
  public BigDecimalType (NumberSign sign, int precision, int decimals) {
    super (sign, precision, decimals);
  }


  @Override
  public BigDecimal createFromString(String source) throws UserEntryException {
    validateDecimalSource(source);
    return new BigDecimal(source);
  }


  @Override
  public BigDecimal newInstance(String source) {
    return new BigDecimal(source);
  }


  @Override
  public BigDecimal primalValue() {
    return BigDecimal.ZERO;
  }
  
  
  @Override
  public JsonType getJsonType () {
    return JsonType.PLAIN_STRING;
  }
  
  
  @Override
  protected void validate(BigDecimal value) throws UserEntryException {
    if (getNumberSign() == NumberSign.UNSIGNED && value.compareTo(BigDecimal.ZERO) < 0) {
      throw new UserEntryException("negative number not allowed");
    }
    // The following may truncate very large numbers
    validatePrecision(value.longValue());
    validateDecimals(value.remainder(BigDecimal.ONE).stripTrailingZeros().scale());
  }


  @Override
  public BigDecimal newValue() {
    return BigDecimal.ZERO;
  }


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, BigDecimal value) throws SQLException {
    stmt.setBigDecimal(sqlIndex, value);
  }


  @Override
  public BigDecimal getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    return resultSet.getBigDecimal(sqlIndex);
  }

}
