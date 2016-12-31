package org.gyfor.berkeleydb;

import com.sleepycat.je.DatabaseEntry;

public class DatabaseEntryFields implements IDatabaseEntryFields {

  private final int[] flags;
  
  private byte[] data;
  private int[] offsets;
  private int[] lengths;
  
  
  public DatabaseEntryFields (int... flags) {
    this.flags = flags;
  }

  
  @Override
  public void parse (DatabaseEntry databaseEntry) {
    data = databaseEntry.getData();

    int position = 0;
    offsets = new int[flags.length];
    lengths = new int[flags.length];
    
    for (int index = 0; index < flags.length; index++) {
      offsets[index] = position;
      switch (flags[index]) {
      case NUL_TERMINATED :
        position = IDatabaseEntryFields.skipString(data, position);
        break;
      case UTF8 :
        position = IDatabaseEntryFields.skipUTF8(data, position);
        break;
      case UTF8_LENGTH :
        int v = IDatabaseEntryFields.getUTF8(data, position);
        position += v;
        break;
      case SHORT_LENGTH :
        int v1 = IDatabaseEntryFields.getShort(data, position);
        position += v1;
        break;
      case INT_LENGTH :
        int v2 = IDatabaseEntryFields.getInt(data, position);
        position += v2;
        break;
      default :
        position += flags[index];
        break;
      }
      lengths[index] = position - offsets[index];
    }
  }
  
  
  @Override
  public int getFieldLength(int index) {
    return lengths[index];
  }

  
  @Override
  public int copyTo(byte[] destData, int offset, int index) {
    int length = lengths[index];
    System.arraycopy(data, offsets[index], destData, offset, length);
    return length;
  }

}
