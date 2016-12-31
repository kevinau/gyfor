package org.gyfor.dbloader.berkeley;

import org.gyfor.berkeleydb.IDatabaseEntryFields;

import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

public class FieldedKeyCreator implements SecondaryKeyCreator {

  private final IDatabaseEntryFields entryFields;
  private final int[] fieldIndexes;


  public FieldedKeyCreator(IDatabaseEntryFields entryFields, int... fieldIndexes) {
    this.entryFields = entryFields;
    this.fieldIndexes = fieldIndexes;
  }


  @Override
  public boolean createSecondaryKey(SecondaryDatabase secDb, DatabaseEntry keyEntry, DatabaseEntry dataEntry, DatabaseEntry resultEntry) {
    entryFields.parse(dataEntry);
    
    int resultLength = 0;
    for (int fi : fieldIndexes) {
      resultLength += entryFields.getFieldLength(fi);
    }

    byte[] resultData = new byte[resultLength];
    int offset = 0;
    for (int fi : fieldIndexes) {
      offset += entryFields.copyTo(resultData, offset, fi);
    }
    resultEntry.setData(resultData);
    return true;
  }
}
