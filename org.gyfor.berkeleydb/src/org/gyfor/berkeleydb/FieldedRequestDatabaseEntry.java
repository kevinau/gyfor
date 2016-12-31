package org.gyfor.berkeleydb;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import org.gyfor.math.Decimal;


public class FieldedRequestDatabaseEntry extends RequestDatabaseEntry implements IDatabaseEntryFields {

  private static final long serialVersionUID = 1L;

  private static final int INITIAL_FIELD_SIZE = 32;

  private int[] fieldOffsets = new int[INITIAL_FIELD_SIZE];
  private int[] fieldLengths = new int[INITIAL_FIELD_SIZE];
  private int index = 0;


  public FieldedRequestDatabaseEntry() {
    super ();
  }
  
  
  public FieldedRequestDatabaseEntry(int capacity) {
    super (capacity);
  }


  @Override
  public RequestDatabaseEntry clear() {
    position = 0;
    setSize(0);
    return this;
  }


  private void setFieldOffset () {
    if (fieldOffsets.length >= index) {
      // Double the length of offsets and lengths
      fieldOffsets = Arrays.copyOf(fieldOffsets, fieldOffsets.length * 2);
      fieldLengths = Arrays.copyOf(fieldLengths, fieldLengths.length * 2);
    }
    fieldOffsets[index] = position;
  }
  
  
  private void setFieldLength () {
    fieldLengths[index] = position - fieldOffsets[index];
    index++;
  }
  
  
  @Override
  public void putBoolean(boolean v) {
    setFieldOffset();
    super.putBoolean(v);
    setFieldLength();
  }
  
  
  @Override
  public void putByte(byte v) {
    setFieldOffset();
    super.putByte(v);
    setFieldLength();
  }

  
  @Override
  public void putChar(char v) {
    setFieldOffset();
    super.putChar(v);
    setFieldLength();
  }
  
  
  @Override
  public void putDate (Date v) {
    setFieldOffset();
    super.putDate(v);
    setFieldLength();
  }


  @Override
  public void putDecimal (Decimal v) {
    setFieldOffset();
    super.putDecimal(v);
    setFieldLength();
  }


  @Override
  public void putDouble(double v) {
    setFieldOffset();
    super.putDouble(v);
    setFieldLength();
  }


  @Override
  public void putEnum(Enum<?> v) {
    setFieldOffset();
    super.putEnum(v);
    setFieldLength();
  }


  @Override
  public void putFloat(float v) {
    setFieldOffset();
    super.putFloat(v);
    setFieldLength();
  }


  @Override
  public void putInt(int v) {
    setFieldOffset();
    super.putInt(v);
    setFieldLength();
  }


  @Override
  public void putLocalDate (LocalDate v) {
    setFieldOffset();
    super.putLocalDate(v);
    setFieldLength();
  }


  @Override
  public void putLong(long v) {
    setFieldOffset();
    super.putLong(v);
    setFieldLength();
  }


  @Override
  public void putRightJustifiedString(String v) {
    setFieldOffset();
    super.putRightJustifiedString(v);
    setFieldLength();
  }


  @Override
  public void putShort(short v) {
    setFieldOffset();
    super.putShort(v);
    setFieldLength();
  }

  
  @Override
  public void putSQLDate (java.sql.Date v) {
    setFieldOffset();
    super.putSQLDate(v);
    setFieldLength();
  }

  
  @Override
  public void putString(String v) {
    setFieldOffset();
    super.putString(v);
    setFieldLength();
  }
  
  
  @Override
  public void putUTF8 (int v) {
    setFieldOffset();
    super.putUTF8(v);
    setFieldLength();
  }

  
  @Override
  public String toString () {
    StringBuilder s = new StringBuilder();
    byte[] data = getData();
    for (int i = 0; i < getSize(); i++) {
      if (i > 0) {
        s.append(' ');
      }
      int b = ((int)data[i]) & 0xff;
      if (b < 16) {
        s.append('0');
      }
      s.append(Integer.toHexString(b));
    }
    return s.toString();
  }

  
  @Override
  public int getFieldLength (int i) {
    if (i >= index) {
      throw new ArrayIndexOutOfBoundsException(i);
    }
    return fieldLengths[i];
  }
  
  
  @Override
  public int copyTo (byte[] srcData, byte[] destData, int destOffset, int fieldIndex) {
    int length = fieldLengths[fieldIndex];
    System.arraycopy(srcData, fieldOffsets[fieldIndex], destData, destOffset, length);
    return length;
  }
  
}
