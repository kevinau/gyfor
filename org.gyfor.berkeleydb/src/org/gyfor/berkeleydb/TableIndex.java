package org.gyfor.berkeleydb;

import org.gyfor.object.plan.IEntityPlan;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.SecondaryKeyCreator;

public class TableIndex {

  private final DataTable owningTable;
  
  /**
   * A one based number that identifies a table index.
   */
  private final int index;
  private final int[] bufferFieldSizes;
  private final SecondaryDatabase secondaryDatabase;
  
  private final int[] keyFieldIndexes;
  
  
  TableIndex (DataTable dataTable, int[] bufferFieldSizes, int n, boolean readOnly) {
    this.owningTable = dataTable;
    this.bufferFieldSizes = bufferFieldSizes;
    this.index = n;
    
    this.keyFieldIndexes = dataTable.getKeyFieldIndexes(n);
    
    SecondaryConfig secondaryConfig = new SecondaryConfig();
    secondaryConfig.setAllowCreate(true);
    secondaryConfig.setTransactional(true);
    secondaryConfig.setAllowPopulate(true);
    secondaryConfig.setSortedDuplicates(false);
    secondaryConfig.setReadOnly(readOnly);
    SecondaryKeyCreator keyCreator1 = new FieldedKeyCreator();
    secondaryConfig.setKeyCreator(keyCreator1);
    
    DataEnvironment dataEnvironment = dataTable.getDataEnvironment();
    Database database = dataTable.getDatabase();
    IEntityPlan<?> entityPlan = dataTable.getEntityPlan();
    secondaryDatabase = dataEnvironment.openSecondaryDatabase(null, entityPlan.getClassName() + "_" + n, database, secondaryConfig); 
  }
  
  
  private class FieldedKeyCreator implements SecondaryKeyCreator {
  
    private FieldedKeyCreator () {
    }

    @Override
    public boolean createSecondaryKey(SecondaryDatabase secDb, DatabaseEntry keyEntry, DatabaseEntry dataEntry, DatabaseEntry resultEntry) {
      FieldedDatabaseEntry fde = new FieldedDatabaseEntry(bufferFieldSizes, dataEntry);
      
      int resultLength = 0;
      for (int fi : keyFieldIndexes) {
        resultLength += fde.getFieldLength(fi);
      }

      byte[] resultData = new byte[resultLength];
      int offset = 0;
      for (int fi : keyFieldIndexes) {
        offset += fde.copyTo(resultData, offset, fi);
      }
      resultEntry.setData(resultData);
      return true;
    }
  }
  
  
  SecondaryCursor openCursor() {
    return secondaryDatabase.openCursor(null, null);
  }
  
  
  void close() {
    secondaryDatabase.close();
  }
  
}
