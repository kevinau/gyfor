package org.gyfor.object.test.data;

import java.math.BigDecimal;

import org.gyfor.math.Decimal;
import org.plcore.userio.IOField;


public class DecimalBased {

  @IOField
  public float float1;

  @IOField
  public double double1;

  @IOField
  public BigDecimal bigDecimal;

  @IOField
  public Decimal decimal;

  public float getFloat1() {
    return float1;
  }

  public void setFloat1(float float1) {
    this.float1 = float1;
  }

  public double getDouble1() {
    return double1;
  }

  public void setDouble1(double double1) {
    this.double1 = double1;
  }

  public BigDecimal getBigDecimal() {
    return bigDecimal;
  }

  public void setBigDecimal(BigDecimal bigDecimal) {
    this.bigDecimal = bigDecimal;
  }

  public Decimal getDecimal() {
    return decimal;
  }

  public void setDecimal(Decimal decimal) {
    this.decimal = decimal;
  }

}
