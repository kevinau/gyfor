package org.gyfor.berkeleydb;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import org.gyfor.math.Decimal;


public class SortableByteBuffer implements Comparable<SortableByteBuffer> {

  private static final int DEFAULT_CAPACITY = 64;
  
  byte[] bytes;
  int position;
  int limit;


  public SortableByteBuffer() {
    this(DEFAULT_CAPACITY);
  }


  public SortableByteBuffer(int capacity) {
    bytes = new byte[capacity];
    position = 0;
    limit = 0;
  }


  @Override
  public int compareTo(SortableByteBuffer other) {
    if (this == other) {
      return 0;
    }
    int n = Math.min(limit, other.limit);
    for (int i = 0; i < n; i++) {
      if (bytes[i] != other.bytes[i]) {
        int b0 = ((int)bytes[i]) & 0xff;
        int b1 = ((int)other.bytes[i]) & 0xff;
        return b0 < b1 ? -1 : +1;
      }
    }
    if (limit == other.limit) {
      return 0;
    } else if (limit < other.limit) {
      return -1;
    } else {
      return +1;
    }
  }


  private void ensureCapacity(int n) {
    int nx = position + n;
    if (nx > bytes.length) {
      bytes = Arrays.copyOf(bytes, bytes.length * 2);
    }
  }


  public SortableByteBuffer flip() {
    limit = position;
    position = 0;
    return this;
  }


  public boolean getBoolean() {
    byte v = bytes[position++];
    return (v == 1);
  }


  public byte getByte() {
    if (position + Byte.BYTES > limit) {
      throw new ArrayIndexOutOfBoundsException();
    }
    byte v = bytes[position++];
    // Reverse the sign bit that was stored
    v ^= ~Byte.MAX_VALUE;
    return v;
  }


  public char getChar() {
    int v = getUTF8();
    return (char)v;
  }


  public double getDouble() {
    if (position + Double.BYTES > limit) {
      throw new ArrayIndexOutOfBoundsException();
    }
    long v = bytes[position++];
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    // Reverse the sign bit that was stored 
    v ^= (v >> 63) & Long.MAX_VALUE;
    return Double.longBitsToDouble(v);
  }


  public float getFloat() {
    if (position + Float.BYTES > limit) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int v = bytes[position++];
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    // Reverse the sign bit that was stored 
    v ^= (v >> 31) & Integer.MAX_VALUE;
    return Float.intBitsToFloat(v);
  }


  private int getRawInt() {
    if (position + Integer.BYTES > limit) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int v = bytes[position++];
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    return v;
  }


  public int getInt() {
    int v = getRawInt();
    // Reverse the sign bit that was stored
    v ^= ~Integer.MAX_VALUE;
    return v;
  }


  public long getLong() {
    if (position + Long.BYTES > limit) {
      throw new ArrayIndexOutOfBoundsException();
    }
    long v = bytes[position++];
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    v = (v << 8) + (bytes[position++] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Long.MAX_VALUE;
    return v;
  }

  
  public short getShort() {
    if (position + Short.BYTES > limit) {
      throw new ArrayIndexOutOfBoundsException(position + Short.BYTES);
    }
    int v = bytes[position++];
    v = (v << 8) + (bytes[position++] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Short.MAX_VALUE;
    return (short)v;
  }
  
  
  public String getString() {
    int i = position;
    while (i < bytes.length && bytes[i] != 0) {
      i++;
    }
    if (i == bytes.length) {
      throw new ArrayIndexOutOfBoundsException(i);
    }
    String v = new String(bytes, position, i - position, StandardCharsets.UTF_8);
    position = i + 1;
    return v;
  }
  
  
  public int getUTF8 () {
    if (position + 1 > limit) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int b = bytes[position++] & 0xff;
    if ((b & 0b1000_0000) == 0) {
      return b;
    } else if ((b & 0b1110_0000) == 0b1100_0000) {
      if (position + 1 > limit) {
        throw new ArrayIndexOutOfBoundsException();
      }
      int b2 = bytes[position++] & 0x3f;
      return ((b & 0x1f) << 6) | b2;
    } else if ((b & 0b1111_0000) == 0b1110_0000) {
      if (position + 2 > limit) {
        throw new ArrayIndexOutOfBoundsException();
      }
      int b2 = bytes[position++] & 0x3f;
      int b3 = bytes[position++] & 0x3f;
      return ((b & 0xf) << 12) | (b2 << 6) | b3;
    } else {
      if (position + 3 > limit) {
        throw new ArrayIndexOutOfBoundsException();
      }
      int b2 = bytes[position++] & 0x3f;
      int b3 = bytes[position++] & 0x3f;
      int b4 = bytes[position++] & 0x3f;
      return ((b & 0x7) << 18) | (b2 << 12) | (b3 << 6) | b4;
    }    
  }

  
  public void putBoolean(boolean v) {
    ensureCapacity(1);
    bytes[position++] = (v ? (byte)1 : (byte)0);
  }


  public void putByte(byte v) {
    ensureCapacity(Byte.BYTES);
    // Reverse the sign bit to allow byte sorting
    v ^= ~Byte.MAX_VALUE;
    bytes[position++] = v;
  }


  public void putChar(char v) {
    putUTF8((int)v);
  }

  
  public void putDate (Date v) {
    putLong(v.getTime());
  }

  
  public Date getDate () {
    long n = getLong();
    return new Date(n);
  }
  
  
  public void putSQLDate (java.sql.Date v) {
    putLong(v.getTime());
  }

  
  public java.sql.Date getSQLDate () {
    long n = getLong();
    return new java.sql.Date(n);
  }
  
  
  public void putLocalDate (LocalDate v) {
    putLong(v.toEpochDay());
  }

  
  public LocalDate getLocalDate () {
    long n = getLong();
    return LocalDate.ofEpochDay(n);
  }
  
  
  public void putDouble(double v) {
    ensureCapacity(Double.BYTES);

    long v1 = Double.doubleToRawLongBits(v);
    // Reverse the sign bit to allow byte sorting negative doubles have bits 0-30
    // inverted (because you want the opposite order to what the original
    // sign/magnitude representation would give you, whilst preserving the sign
    // bit.
    v1 ^= (v1 >> 63) & Long.MAX_VALUE;
    bytes[position++] = (byte)((v1 >>> 56) & 0xff);
    bytes[position++] = (byte)((v1 >>> 48) & 0xff);
    bytes[position++] = (byte)((v1 >>> 40) & 0xff);
    bytes[position++] = (byte)((v1 >>> 32) & 0xff);
    bytes[position++] = (byte)((v1 >>> 24) & 0xff);
    bytes[position++] = (byte)((v1 >>> 16) & 0xff);
    bytes[position++] = (byte)((v1 >>> 8) & 0xff);
    bytes[position++] = (byte)(v1 & 0xff);
  }


  public void putEnum(Enum<?> e) {
    short v = (short)e.ordinal();
    putShort(v);
  }


  public void putFloat(float v) {
    ensureCapacity(Float.BYTES);

    int v1 = Float.floatToRawIntBits(v);
    // Reverse the sign bit to allow byte sorting negative floats have bits 0-30
    // inverted (because you want the opposite order to what the original
    // sign/magnitude representation would give you, whilst preserving the sign
    // bit.
    v1 ^= (v1 >> 31) & Integer.MAX_VALUE;
    bytes[position++] = (byte)((v1 >>> 24) & 0xff);
    bytes[position++] = (byte)((v1 >>> 16) & 0xff);
    bytes[position++] = (byte)((v1 >>> 8) & 0xff);
    bytes[position++] = (byte)(v1 & 0xff);
  }


  public void putInt(int v) {
    // Reverse the sign bit to allow byte sorting
    v ^= ~Integer.MAX_VALUE;
    putRawInt (v);
  }


  private void putRawInt(int v) {
    ensureCapacity(Integer.BYTES);
    bytes[position++] = (byte)((v >>> 24) & 0xff);
    bytes[position++] = (byte)((v >>> 16) & 0xff);
    bytes[position++] = (byte)((v >>> 8) & 0xff);
    bytes[position++] = (byte)(v & 0xff);
  }


  public void putLong(long v) {
    ensureCapacity(Long.BYTES);
    // Reverse the sign bit to allow byte sorting
    v ^= ~Long.MAX_VALUE;
    bytes[position++] = (byte)((v >>> 56) & 0xff);
    bytes[position++] = (byte)((v >>> 48) & 0xff);
    bytes[position++] = (byte)((v >>> 40) & 0xff);
    bytes[position++] = (byte)((v >>> 32) & 0xff);
    bytes[position++] = (byte)((v >>> 24) & 0xff);
    bytes[position++] = (byte)((v >>> 16) & 0xff);
    bytes[position++] = (byte)((v >>> 8) & 0xff);
    bytes[position++] = (byte)(v & 0xff);
  }


  public void putShort(short v) {
    ensureCapacity(Short.BYTES);
    // Reverse the sign bit to allow byte sorting
    v ^= ~Short.MAX_VALUE;
    bytes[position++] = (byte)((v >>> 8) & 0xff);
    bytes[position++] = (byte)(v & 0xff);
  }


  public void putString(String v) {
    byte[] vx = v.getBytes(StandardCharsets.UTF_8);
    ensureCapacity(vx.length + 1);
    System.arraycopy(vx, 0, bytes, position, vx.length);
    position += vx.length;
    bytes[position++] = 0;
  }


  public void putRightJustifiedString(String v) {
    byte[] vx = v.getBytes(StandardCharsets.UTF_8);
    putUTF8(vx.length);
    ensureCapacity(vx.length);
    System.arraycopy(vx, 0, bytes, position, vx.length);
    position += vx.length;
  }


  public String getRightJustifiedString () {
    int n = getUTF8();
    if (position + n >= limit) {
      throw new ArrayIndexOutOfBoundsException(position + n);
    }
    String s = new String(bytes, position, n, StandardCharsets.UTF_8);
    position += n;
    return s;
  }


  public void putUTF8 (int v) {
    ensureCapacity(4);
    if (v <= 0x7f) {
      bytes[position++] = (byte)(v & 0xff);
    } else if (v <= 0x7ff) {
      bytes[position++] = (byte)(0b1100_0000 | (v >>> 6) & 0x1f);
      bytes[position++] = (byte)(0b1000_0000 | (v & 0x3f));
    } else if (v <= 0xffff) {
      bytes[position++] = (byte)(0b1110_0000 | (v >>> 12) & 0x0f);
      bytes[position++] = (byte)(0b1000_0000 | (v >>> 6) & 0x3f);
      bytes[position++] = (byte)(0b1000_0000 | (v & 0x3f));
    } else {
      bytes[position++] = (byte)(0b1111_0000 | (v >>> 18) & 0x7);
      bytes[position++] = (byte)(0b1000_0000 | (v >>> 12) & 0x3f);
      bytes[position++] = (byte)(0b1000_0000 | (v >>> 6) & 0x3f);
      bytes[position++] = (byte)(0b1000_0000 | (v & 0x3f));
    }
  }

  
  public void putDecimal (Decimal v) {
    ensureCapacity(Integer.BYTES + Long.BYTES);

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

  
  public Decimal getDecimal () {
    if (Integer.BYTES + Long.BYTES > limit) {
      throw new ArrayIndexOutOfBoundsException();
    }
    int s1 = getInt();
    long v1 = getLong();
    if (s1 < 0) {
      s1 = -s1;
    }
    s1 = 64 - s1;
    Decimal v = new Decimal(v1, s1).trim();
    return v;
  }
  
  
  public SortableByteBuffer rewind() {
    position = 0;
    return this;
  }

  
  public SortableByteBuffer clear() {
    limit = 0;
    position = 0;
    return this;
  }

  
  @Override
  public String toString () {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < limit; i++) {
      if (i > 0) {
        s.append(' ');
      }
      int b = ((int)bytes[i]) & 0xff;
      if (b < 16) {
        s.append('0');
      }
      s.append(Integer.toHexString(b));
    }
    return s.toString();
  }

}
