package org.gyfor.dao.berkeley;

import org.gyfor.util.SimpleBuffer;

import com.sleepycat.je.DatabaseEntry;

public class NameDatabaseEntry extends DatabaseEntry {

  private static final long serialVersionUID = 1L;

  
  public NameDatabaseEntry () {
  }
  
  
  public NameDatabaseEntry (String name) {
    putName (name);
  }

  
  public void putName (String name) {
    SimpleBuffer buffer = new SimpleBuffer(0);
    buffer.appendNulTerminatedString(name);
    super.setData(buffer.bytes(), 0, buffer.size());
  }
  
  
  public String getName () {
    byte[] data = super.getData();
    SimpleBuffer buffer = new SimpleBuffer(data);
    return buffer.nextNulTerminatedString();
  }

}
