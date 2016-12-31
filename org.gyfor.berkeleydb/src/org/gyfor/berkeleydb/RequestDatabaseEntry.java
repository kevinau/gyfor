package org.gyfor.berkeleydb;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import org.gyfor.math.Decimal;

import com.sleepycat.je.DatabaseEntry;


public class RequestDatabaseEntry extends DatabaseEntry {

  private static final long serialVersionUID = 1L;

  private static final int DEFAULT_CAPACITY = 64;
  
  protected int position;


  public RequestDatabaseEntry() {
    this(DEFAULT_CAPACITY);
  }
  
  
  public RequestDatabaseEntry(int capacity) {
    setData(new byte[capacity], 0, capacity);
    position = 0;
  }


  public RequestDatabaseEntry clear() {
    position = 0;
    setSize(0);
    return this;
  }


  private byte[] ensureCapacity(int n) {
    byte[] data = getData();
    int nx = position + n;
    if (nx > data.length) {
      data = Arrays.copyOf(data, data.length * 2);
      setData(data);
    }
    return data;
  }

  

  public void putBoolean(boolean v) {
    byte[] data = ensureCapacity(1);
    
    data[position++] = (v ? (byte)1 : (byte)0);
    
    if (position > getSize()) {
      setSize(position);
    }
  }
  
  
  public void putByte(byte v) {
    byte[] data = ensureCapacity(Byte.BYTES);
    
    // Reverse the sign bit to allow byte sorting
    v ^= ~Byte.MAX_VALUE;
    data[position++] = v;
    
    if (position > getSize()) {
      setSize(position);
    }
  }

  
  public void putByteArray (byte[] v, int offset, int length) {
    byte[] data = ensureCapacity(length);
    
    putInt(length);
    System.arraycopy(data, position, v,  offset, length);
    position += length;
    
    if (position > getSize()) {
      setSize(position);
    }
  }

  
  public void putChar(char v) {
    putUTF8((int)v);
  }
  
  
  public void putDate (Date v) {
    putLong(v.getTime());
  }


  public void putDecimal (Decimal v) {
    Decimal normal = v.normalize();
    long v1 = normal.getRawLong();
    // The scale here is positive, with a higher number representing a higher value
    int s1 = 64 - normal.getScale();

    if (v1 < 0) {
      s1 = -s1;
    }
    putInt (s1);
    putLong (v1);
  }


  public void putDouble(double v) {
    byte[] data = ensureCapacity(Double.BYTES);

    long v1 = Double.doubleToRawLongBits(v);
    // Reverse the sign bit to allow byte sorting negative doubles have bits 0-30
    // inverted (because you want the opposite order to what the original
    // sign/magnitude representation would give you, whilst preserving the sign
    // bit.
    v1 ^= (v1 >> 63) & Long.MAX_VALUE;
    data[position++] = (byte)((v1 >>> 56) & 0xff);
    data[position++] = (byte)((v1 >>> 48) & 0xff);
    data[position++] = (byte)((v1 >>> 40) & 0xff);
    data[position++] = (byte)((v1 >>> 32) & 0xff);
    data[position++] = (byte)((v1 >>> 24) & 0xff);
    data[position++] = (byte)((v1 >>> 16) & 0xff);
    data[position++] = (byte)((v1 >>> 8) & 0xff);
    data[position++] = (byte)(v1 & 0xff);
    
    if (position > getSize()) {
      setSize(position);
    }
  }


  public void putEnum(Enum<?> e) {
    short v = (short)e.ordinal();
    putShort(v);
  }


  public void putFloat(float v) {
    byte[] data = ensureCapacity(Float.BYTES);

    int v1 = Float.floatToRawIntBits(v);
    // Reverse the sign bit to allow byte sorting negative floats have bits 0-30
    // inverted (because you want the opposite order to what the original
    // sign/magnitude representation would give you, whilst preserving the sign
    // bit.
    v1 ^= (v1 >> 31) & Integer.MAX_VALUE;
    data[position++] = (byte)((v1 >>> 24) & 0xff);
    data[position++] = (byte)((v1 >>> 16) & 0xff);
    data[position++] = (byte)((v1 >>> 8) & 0xff);
    data[position++] = (byte)(v1 & 0xff);
    
    if (position > getSize()) {
      setSize(position);
    }
  }


  public void putInt(int v) {
    // Reverse the sign bit to allow byte sorting
    v ^= ~Integer.MAX_VALUE;
    putRawInt (v);
  }


  public void putLocalDate (LocalDate v) {
    putLong(v.toEpochDay());
  }


  public void putLong(long v) {
    byte[] data = ensureCapacity(Long.BYTES);
    
    // Reverse the sign bit to allow byte sorting
    v ^= ~Long.MAX_VALUE;
    data[position++] = (byte)((v >>> 56) & 0xff);
    data[position++] = (byte)((v >>> 48) & 0xff);
    data[position++] = (byte)((v >>> 40) & 0xff);
    data[position++] = (byte)((v >>> 32) & 0xff);
    data[position++] = (byte)((v >>> 24) & 0xff);
    data[position++] = (byte)((v >>> 16) & 0xff);
    data[position++] = (byte)((v >>> 8) & 0xff);
    data[position++] = (byte)(v & 0xff);
    
    if (position > getSize()) {
      setSize(position);
    }
  }


  private void putRawInt(int v) {
    byte[] data = ensureCapacity(Integer.BYTES);
    
    data[position++] = (byte)((v >>> 24) & 0xff);
    data[position++] = (byte)((v >>> 16) & 0xff);
    data[position++] = (byte)((v >>> 8) & 0xff);
    data[position++] = (byte)(v & 0xff);
    
    if (position > getSize()) {
      setSize(position);
    }
  }


  public void putRightJustifiedString(String v) {
    byte[] data = ensureCapacity(Integer.BYTES);
    
    byte[] vx = v.getBytes(StandardCharsets.UTF_8);
    putUTF8(vx.length);
    ensureCapacity(vx.length);
    System.arraycopy(vx, 0, data, position, vx.length);
    position += vx.length;
    
    if (position > getSize()) {
      setSize(position);
    }
  }


  public void putShort(short v) {
    byte[] data = ensureCapacity(Short.BYTES);
    
    // Reverse the sign bit to allow byte sorting
    v ^= ~Short.MAX_VALUE;
    data[position++] = (byte)((v >>> 8) & 0xff);
    data[position++] = (byte)(v & 0xff);
    
    if (position > getSize()) {
      setSize(position);
    }
}

  
  public void putSQLDate (java.sql.Date v) {
    putLong(v.getTime());
  }

  
  public void putString(String v) {
    byte[] vx = v.getBytes(StandardCharsets.UTF_8);
    byte[] data = ensureCapacity(vx.length + 1);

    System.arraycopy(vx, 0, data, position, vx.length);
    position += vx.length;
    data[position++] = 0;
    
    if (position > getSize()) {
      setSize(position);
    }
  }
  
  
  public void putUTF8 (int v) {
    byte[] data = ensureCapacity(4);
    
    if (v <= 0x7f) {
      data[position++] = (byte)(v & 0xff);
    } else if (v <= 0x7ff) {
      data[position++] = (byte)(0b1100_0000 | (v >>> 6) & 0x1f);
      data[position++] = (byte)(0b1000_0000 | (v & 0x3f));
    } else if (v <= 0xffff) {
      data[position++] = (byte)(0b1110_0000 | (v >>> 12) & 0x0f);
      data[position++] = (byte)(0b1000_0000 | (v >>> 6) & 0x3f);
      data[position++] = (byte)(0b1000_0000 | (v & 0x3f));
    } else {
      data[position++] = (byte)(0b1111_0000 | (v >>> 18) & 0x7);
      data[position++] = (byte)(0b1000_0000 | (v >>> 12) & 0x3f);
      data[position++] = (byte)(0b1000_0000 | (v >>> 6) & 0x3f);
      data[position++] = (byte)(0b1000_0000 | (v & 0x3f));
    }
    
    if (position > getSize()) {
      setSize(position);
    }
  }

  
  public RequestDatabaseEntry rewind() {
    position = 0;
    return this;
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

}
