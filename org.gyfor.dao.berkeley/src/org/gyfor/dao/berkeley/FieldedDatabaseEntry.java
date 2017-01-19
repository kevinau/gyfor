package org.gyfor.dao.berkeley;

import org.gyfor.object.type.IType;

import com.sleepycat.je.DatabaseEntry;

public class FieldedDatabaseEntry {

  private byte[] data;
  private int[] offsets;
  private int[] lengths;
  
  public FieldedDatabaseEntry (int[] flags, DatabaseEntry databaseEntry) {
    data = databaseEntry.getData();

    int position = 0;
    offsets = new int[flags.length];
    lengths = new int[flags.length];
    
    for (int index = 0; index < flags.length; index++) {
      offsets[index] = position;
      switch (flags[index]) {
      case IType.BUFFER_NUL_TERMINATED :
        position = IDatabaseEntryFields.skipString(data, position);
        break;
      case IType.BUFFER_UTF8 :
        position = IDatabaseEntryFields.skipUTF8(data, position);
        break;
      case IType.BUFFER_UTF8_LENGTH :
        int v = IDatabaseEntryFields.getUTF8(data, position);
        position += v;
        break;
      case IType.BUFFER_SHORT_LENGTH :
        int v1 = IDatabaseEntryFields.getShort(data, position);
        position += v1;
        break;
      case IType.BUFFER_INT_LENGTH :
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
  
  
  public int getFieldLength(int index) {
    return lengths[index];
  }

  
  public int copyTo(byte[] destData, int offset, int index) {
    int length = lengths[index];
    System.arraycopy(data, offsets[index], destData, offset, length);
    return length;
  }

}
