package org.gyfor.berkeleydb;

import com.sleepycat.je.DatabaseEntry;

public interface IDatabaseEntryFields {

  public static final int NUL_TERMINATED = -1;
  public static final int UTF8 = -2;
  public static final int UTF8_LENGTH = -3;
  public static final int SHORT_LENGTH = -4;
  public static final int INT_LENGTH = -5;
  

  public static int getInt (byte[] data, int position) {
    int v = data[position++];
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    v = (v << 8) + (data[position++] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Integer.MAX_VALUE;
    return v;
  }


  public static short getShort(byte[] data, int position) {
    int v = data[position++];
    v = (v << 8) + (data[position++] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Short.MAX_VALUE;
    return (short)v;
  }
  
  
  public static int skipString (byte[] data, int position) {
    while (position < data.length && data[position] != 0) {
      position++;
    }
    if (position == data.length) {
      throw new ArrayIndexOutOfBoundsException(position);
    }
    return position + 1;
  }
  
  
  public static int getUTF8 (byte[] data, int position) {
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

  
  public static int skipUTF8 (byte[] data, int position) {
    int b = data[position] & 0xff;
    if ((b & 0b1000_0000) == 0) {
      return position + 1;
    } else if ((b & 0b1110_0000) == 0b1100_0000) {
      return position + 2;
    } else if ((b & 0b1111_0000) == 0b1110_0000) {
      return position + 3;
    } else {
      return position + 4;
    }    
  }


  public int getFieldLength (int fieldIndex);

  public int copyTo (byte[] destData, int offset, int fieldIndex);

  public void parse (DatabaseEntry databaseEntry);
  
}
