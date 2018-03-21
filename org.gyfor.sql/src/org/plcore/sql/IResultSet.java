package org.plcore.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Timestamp;
import java.time.LocalDate;

import org.plcore.math.Decimal;


public interface IResultSet extends AutoCloseable {

  //public Object getObject(int i, int[] colTypes);

  public Object getObject(int[] colTypes);

  //public <T> T getEnum(int i, Class<T> enumClass);

  public <T> T getEnum(Class<T> enumClass);

  //public byte[] getBytes(int i);

  public byte[] getBytes();

  //public BigDecimal getBigDecimal(int i);

  public BigDecimal getBigDecimal();

  //public BigInteger getBigInteger(int i);

  public BigInteger getBigInteger();

  //public boolean getBoolean(int i);

  public boolean getBoolean();

  //public int getInt(int i);

  public int getInt();

  //public long getLong(int i);

  public long getLong();

  //public short getShort(int i);

  public short getShort();

  //public Decimal getDecimal(int i);

  public Decimal getDecimal();

  //public float getFloat(int i);

  public float getFloat();

  //public double getDouble(int i);

  public double getDouble();

  //public java.sql.Date getDate(int i);

  public java.sql.Date getDate();

  //public LocalDate getLocalDate(int i);

  public LocalDate getLocalDate();

  //public URL getURL(int i);

  public URL getURL();

  //public Object getObject(int i);

  public Object getObject();

  //public Blob getBlob(int i);

  public Blob getBlob();

  //public String getString(int i);

  public String getString();

  //public Timestamp getTimestamp(int i);

  public Timestamp getTimestamp();

  public boolean next();

  @Override
  public void close();

  public IResultSetMetaData getMetaData();

}
