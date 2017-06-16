package org.gyfor.dao.mapping;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;

public class TableManipulation {

  protected static final String NL = System.getProperty("line.separator");
  
  protected static String getTableName (String schema, IEntityPlan<?> entityPlan) {
    String tableName = entityPlan.getName();
    if (schema != null) {
      tableName = schema + '.' + tableName;
    }
    return tableName;
  }
   
  
  protected static String buildTableName (String parentTableName, INodePlan elementPlan, int dimension) {
    String tableName;
    switch (dimension) {
    case 0 :
      tableName = parentTableName + '_' + elementPlan.getName();
      break;
    case 1 :
      tableName = parentTableName + '_' + dimension;
      break;
    default :
      int n = parentTableName.lastIndexOf('_');
      tableName = parentTableName.substring(0, n) + '_' + dimension;
      break;
    }
    return tableName;
  }

}
