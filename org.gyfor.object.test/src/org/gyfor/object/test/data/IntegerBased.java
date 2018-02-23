package org.gyfor.object.test.data;

import java.math.BigInteger;

import org.gyfor.object.IOField;


public class IntegerBased {

  @IOField
  public Byte byte1;

  @IOField
  public Integer integer1;

  @IOField
  public Short short1;

  @IOField
  public Long long1;

  @IOField
  public BigInteger bigInteger;

  public Byte getByte1() {
    return byte1;
  }

  public void setByte1(Byte byte1) {
    this.byte1 = byte1;
  }

  public Integer getInteger1() {
    return integer1;
  }

  public void setInteger1(Integer integer1) {
    this.integer1 = integer1;
  }

  public Short getShort1() {
    return short1;
  }

  public void setShort1(Short short1) {
    this.short1 = short1;
  }

  public Long getLong1() {
    return long1;
  }

  public void setLong1(Long long1) {
    this.long1 = long1;
  }

  public BigInteger getBigInteger() {
    return bigInteger;
  }

  public void setBigInteger(BigInteger bigInteger) {
    this.bigInteger = bigInteger;
  }

}
