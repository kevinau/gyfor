package org.gyfor.dao.berkeley;

import java.util.List;

import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.type.IType;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryCursor;
import com.sleepycat.je.Sequence;
import com.sleepycat.je.SequenceConfig;
import com.sleepycat.je.Transaction;

public class DataTable implements AutoCloseable {

  private final DataEnvironment dataEnvironment;
  private final IEntityPlan<?> entityPlan;
  private final boolean readOnly;
  
  private final Database database;
  private final TableIndex[] indexes;
  private final Sequence idSequence;
  
  private String[] bufferFieldNames;
  private int[] bufferFieldSizes;
  
  
  public DataTable (DataEnvironment dataEnvironment, IEntityPlan<?> entityPlan, boolean readOnly) {
    this.dataEnvironment = dataEnvironment;
    this.entityPlan = entityPlan;
    this.readOnly = readOnly;
    
    // Open the database. Create it if it does not already exist.
    DatabaseConfig dbConfig = new DatabaseConfig();
    dbConfig.setAllowCreate(true);
    dbConfig.setTransactional(true);
    dbConfig.setReadOnly(readOnly);
    database = dataEnvironment.openDatabase(null, entityPlan.getClassName(), dbConfig);
    
    List<IItemPlan<?>[]> uniqueConstraints = entityPlan.getUniqueConstraints();
    indexes = new TableIndex[uniqueConstraints.size()];
    
    if (!readOnly) {
      buildFieldFlags();
      for (int i = 0; i < indexes.length; i++) {
        indexes[i] = new TableIndex(this, bufferFieldSizes, i + 1, readOnly);
      }

      SequenceConfig seqConfig = new SequenceConfig().setAllowCreate(true).setInitialValue(1);
      NameDatabaseEntry seqEntry = new NameDatabaseEntry(entityPlan.getClassName() + "_seq");
      idSequence = database.openSequence(null, seqEntry, seqConfig);
    } else {
      idSequence = null;
    }
  }
  
  
  private void buildFieldFlags() {
    if (bufferFieldSizes == null) {
      int[] count = new int[1];
      countBufferFields(entityPlan, count);

      bufferFieldNames = new String[count[0]];
      bufferFieldSizes = new int[count[0]];
      int[] i = new int[1];
      buildFieldFlags(entityPlan, "", bufferFieldNames, bufferFieldSizes, i);
    }
  }


  private void countBufferFields(IClassPlan<?> classPlan, int[] count) {
    INodePlan[] nodePlans = entityPlan.getMemberPlans();

    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case EMBEDDED :
        IClassPlan<?> embeddedPlan = (IClassPlan<?>)nodePlan;
        countBufferFields(embeddedPlan, count);
        break;
      case ITEM :
        count[0]++;
        break;
      default :
        break;
      }
    }
  }


  private void buildFieldFlags(IClassPlan<?> classPlan, String prefix, String[] fieldNames, int[] fieldSizes, int[] i) {
    INodePlan[] nodePlans = entityPlan.getMemberPlans();

    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case EMBEDDED :
        IClassPlan<?> embeddedPlan = (IClassPlan<?>)nodePlan;
        buildFieldFlags(embeddedPlan, prefix + nodePlan.getName() + '.', fieldNames, fieldSizes, i);
        break;
      case ITEM :
        IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
        IType<?> itemType = itemPlan.getType();
        fieldNames[i[0]] = prefix + nodePlan.getName();
        fieldSizes[i[0]] = itemType.getBufferSize();
        i[0]++;
        break;
      default :
        break;
      }
    }
  }


  public ObjectDatabaseEntry getDatabaseEntry () {
    return new ObjectDatabaseEntry(entityPlan);
  }
  
  
  DataEnvironment getDataEnvironment () {
    return dataEnvironment;
  }
  
  
  Database getDatabase () {
    return database;
  }
  
  
  public TableIndex getIndex (int n) {
    buildFieldFlags();
    int i = n - 1;
    if (indexes[i] == null) {
      indexes[i] = new TableIndex(this, bufferFieldSizes, n, readOnly);
    }
    return indexes[i];
  }

  
  int[] getKeyFieldIndexes (int n) {
    IItemPlan<?>[] items = entityPlan.getUniqueConstraints().get(n - 1);
    int[] indexes = new int[items.length];
    for (int i = 0; i < items.length; i++) {
      IItemPlan<?> item = items[i];
      // TODO This is wrong.   
      String path = item.getName();
      indexes[i] = getIndexOf(path);
    }
    return indexes;
  }

  
  private int getIndexOf (String path) {
    for (int j = 0; j < bufferFieldNames.length; j++) {
      if (bufferFieldNames[j].equals(path)) {
        return j;
      }
    }
    throw new IllegalArgumentException(path);
  }
  
  
  public int getNextSequence (Transaction txn) {
    return (int)idSequence.get(txn, 1);
  }
  
  
  public IEntityPlan<?> getEntityPlan() {
    return entityPlan;
  }
 
  
  public OperationStatus put (Transaction txn, DatabaseEntry key, DatabaseEntry data) {
    return database.put(txn, key, data);
  }

  
  public OperationStatus get (Transaction txn, DatabaseEntry key, DatabaseEntry data, LockMode lockMode) {
    return database.get(txn, key, data, lockMode);
  }

  
  public Cursor openCursor() {
    return database.openCursor(null, null);
  }
  
  
  public SecondaryCursor openIndexCursor (int i) {
    return getIndex(i).openCursor();
  }
  
  
  void closeIndex (int n) {
    int i = n - 1;
    if (indexes[i] != null) {
      indexes[i].close();
      indexes[i] = null;
    }
  }

  
  @Override
  public void close () {
    for (int i = 0; i < indexes.length; i++) {
      closeIndex(i + 1);
    }
    database.close();
  }
  
}
