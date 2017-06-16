package org.gyfor.dao.mapping;

import org.gyfor.object.model.IEntityModel;

public class RowManipulation {

  protected static final String NL = System.getProperty("line.separator");
  
  protected static String getTableName (String schema, IEntityModel entityModel) {
    String tableName = entityModel.getName();
    if (schema != null) {
      tableName = schema + '.' + tableName;
    }
    return tableName;
  }
   
}
