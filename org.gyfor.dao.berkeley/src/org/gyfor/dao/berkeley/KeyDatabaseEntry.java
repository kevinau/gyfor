package org.gyfor.dao.berkeley;

import com.sleepycat.je.DatabaseEntry;

public class KeyDatabaseEntry extends DatabaseEntry {

  private static final long serialVersionUID = 1L;

  public KeyDatabaseEntry () {
    super (new byte[4], 0, 4);
  }

  
  public KeyDatabaseEntry (int id) {
    super (new byte[4], 0, 4);
    putInt (id);
  }

  
  public void putInt (int v) {
    // Reverse the sign bit to allow byte sorting
    v ^= ~Integer.MAX_VALUE;
    byte[] data = super.getData();
    data[0] = (byte)((v >>> 24) & 0xff);
    data[1] = (byte)((v >>> 16) & 0xff);
    data[2] = (byte)((v >>> 8) & 0xff);
    data[3] = (byte)(v & 0xff);
  }
  
  
  public int getInt() {
    byte[] data = super.getData();
    int v = data[0];
    v = (v << 8) + (data[1] & 0xff);
    v = (v << 8) + (data[2] & 0xff);
    v = (v << 8) + (data[3] & 0xff);
    // Reverse the sign bit that was stored
    v ^= ~Integer.MAX_VALUE;
    return v;
  }

}
