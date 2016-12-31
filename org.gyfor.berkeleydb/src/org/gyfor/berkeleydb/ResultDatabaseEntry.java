package org.gyfor.berkeleydb;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;

import org.gyfor.math.Decimal;

import com.sleepycat.je.DatabaseEntry;

public class ResultDatabaseEntry extends DatabaseEntry {

  private static final long serialVersionUID = 1L;

  private int position;
  
  
  public ResultDatabaseEntry () {
    position = 0;
  }
  
  
  public boolean getBoolean() {
    byte[] data = getData();
    byte v = data[position++];
    return (v == 1);
  }


  public byte getByte() {
    byte[] data = getData();
    byte v = data[position++];
    // Reverse the sign bit that was stored
    v ^= ~Byte.MAX_VALUE;
    return v;
  }


  public char getChar() {
    int v = getUTF8();
    return (char)v;
  }


  public double getDouble() {
    byte[] data = getData();
    long v = data[position++];
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    // Reverse the sign bit that was stored 
    v ^= (v >> 63) & Long.MAX_VALUE;
    return Double.longBitsToDouble(v);
  }


  public float getFloat() {
    byte[] data = getData();
    int v = data[position++];
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    // Reverse the sign bit that was stored 
    v ^= (v >> 31) & Integer.MAX_VALUE;
    return Float.intBitsToFloat(v);
  }


  public int getInt() {
    byte[] data = getData();
    int v = data[position++];
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Integer.MAX_VALUE;
    return v;
  }
  
  
  public long getLong() {
    byte[] data = getData();
    long v = data[position++];
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Long.MAX_VALUE;
    return v;
  }

  
  public short getShort() {
    byte[] data = getData();
    int v = data[position++];
    v = (v << 8) + (data[position++] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Short.MAX_VALUE;
    return (short)v;
  }
  
  
  public String getString() {
    byte[] data = getData();
    int i = position;
    while (i < data.length && data[i] != 0) {
      i++;
    }
    if (i == data.length) {
      throw new ArrayIndexOutOfBoundsException(i);
    }
    String v = new String(data, position, i - position, StandardCharsets.UTF_8);
    position = i + 1;
    return v;
  }
  
  
  public int getUTF8 () {
    byte[] data = getData();
    int b = data[position++] & 0xff;
    if ((b & 0b1000_0000) == 0) {
      return b;
    } else if ((b & 0b1110_0000) == 0b1100_0000) {
      int b2 = data[position++] & 0x3f;
      return ((b & 0x1f) << 6) | b2;
    } else if ((b & 0b1111_0000) == 0b1110_0000) {
      int b2 = data[position++] & 0x3f;
      int b3 = data[position++] & 0x3f;
      return ((b & 0xf) << 12) | (b2 << 6) | b3;
    } else {
      int b2 = data[position++] & 0x3f;
      int b3 = data[position++] & 0x3f;
      int b4 = data[position++] & 0x3f;
      return ((b & 0x7) << 18) | (b2 << 12) | (b3 << 6) | b4;
    }    
  }

  
  public Date getDate () {
    long n = getLong();
    return new Date(n);
  }
  
  
  public java.sql.Date getSQLDate () {
    long n = getLong();
    return new java.sql.Date(n);
  }
  
  
  public LocalDate getLocalDate () {
    long n = getLong();
    return LocalDate.ofEpochDay(n);
  }
  
  
  public String getRightJustifiedString () {
    int n = getUTF8();
    byte[] data = getData();
    String s = new String(data, position, n, StandardCharsets.UTF_8);
    position += n;
    return s;
  }


  public Decimal getDecimal () {
    int s1 = getInt();
    long v1 = getLong();
    if (s1 < 0) {
      s1 = -s1;
    }
    s1 = 64 - s1;
    Decimal v = new Decimal(v1, s1).trim();
    return v;
  }
  
  
  public ResultDatabaseEntry rewind() {
    position = 0;
    return this;
  }

  
  public ResultDatabaseEntry setPosition (int position) {
    this.position = position;
    return this;
  }
  
  
  @Override
  public String toString () {
    byte[] data = getData();
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
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

  
}