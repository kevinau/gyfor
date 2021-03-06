package org.gyfor.dao.mapping;

import java.util.List;

import org.gyfor.object.plan.IEmbeddedPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.todo.NotYetImplementedException;
import org.gyfor.util.CamelCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SQLBuilder {

  private final String schema;
  private final IEntityPlan<?> entityPlan;
  
  private String tableName;
  private IItemPlan<?> idPlan;
  private IItemPlan<?> versionPlan;
  //private IItemPlan<?>[] keyFields;
  private IItemPlan<?>[] dataPlans;
  private IItemPlan<?> entityLifePlan;
  
  private String nextValSql = null;
  ////private String insertSql = null;
  private String deleteSql = null;
  private String deleteAllSql = null;
  //private String keyUpdateSql = null;
  private String dataUpdateSql = null;
  private String lifeUpdateSql = null;
  private String fetchAllSql = null;
  private String fetchByIdSql = null;
  private String fetchDescriptionByIdSql = null;
  private String fetchDescriptionAllSql = null;
  private String queryUniqueSql[] = null;
  //private String fetchByNaturalKeySql = null;
  //private String queryByNaturalKeySql = null;
  
  private boolean mappingRequired = false;
  private int[] mapping;

  
  private Logger logger = LoggerFactory.getLogger(SQLBuilder.class);
  
  
  public SQLBuilder (String schema, IEntityPlan<?> entityPlan) {
    this.schema = schema;
    this.entityPlan = entityPlan;

    tableName = toSQLName(entityPlan.getEntityName());
    if (schema != null) {
      tableName = schema + "." + tableName;
    }
    
    //buildKeyFields();
    
    idPlan = entityPlan.getIdPlan();
    versionPlan = entityPlan.getVersionPlan();
    List<INodePlan> dx = entityPlan.getDataPlans();
    dataPlans = dx.toArray(new IItemPlan<?>[0]);
    entityLifePlan = entityPlan.getEntityLifePlan();
    
    int n = entityPlan.getUniqueConstraints().size();
    queryUniqueSql = new String[n];
  }


  private static String toSQLName (String name) {
    StringBuffer buffer = new StringBuffer();
    int n = name.length();
    
    int i = 0;
    boolean lastUppercase = false;
    while (i < n) {
      char c = name.charAt(i);
      if (lastUppercase && !Character.isUpperCase(c)) {
        if (i > 1) {
          buffer.insert(i - 1, '_');
        }
      }
      buffer.append(c);
      lastUppercase = Character.isUpperCase(c);
      i++;
    }
    return buffer.toString();
  }

  
//  private static String toSQLName2 (String name) {
//    StringBuffer buffer = new StringBuffer();
//    boolean insertUnderscore = false;
//    int n = name.length();
//    int i = 0;
//    
//    char c = name.charAt(0);
//    if (Character.isUpperCase(c)) {
//      insertUnderscore = false;
//    } else {
//      // Find the first uppercase character (not counting the first character), if any
//      i = 1;
//      while (i < n) {
//        c = name.charAt(i);
//        if (Character.isUpperCase(c)) {
//          insertUnderscore = true;
//          break;
//        }
//        i++;
//      }
//    }
//    
//    if (i == n) {
//      // No upper case character found, so return the name as the SQL name
//      return name;
//    }
//    
//    // Start again, and build the SQL name
//    buffer.append(name, 0, i);
//
//    while (i < n) {
//      c = name.charAt(i);
//      if (i == 0) {
//        // First character is assumed to be lower case
//        buffer.append(c);
//         //buffer.append(Character.toLowerCase(c));
//      } else {
//        if (insertUnderscore && Character.isUpperCase(c)) {
//          buffer.append('_');
//          insertUnderscore = false;
//          buffer.append(Character.toLowerCase(c));
//        } else {
//          buffer.append(c);
//        }
//        if (!Character.isUpperCase(c)) {
//          insertUnderscore = true;
//        }
//      }
//      i++;
//    }
//    return buffer.toString();
//  }
  

//  private void buildKeyFields () {
//    // Get the first unique constraint, and use it as the form key fields
//    Table tableAnn = entityClass.getAnnotation(Table.class);
//    if (tableAnn == null) {
//      // There is not table annotation, so use the first non-special field
//      keyFields = defaultKeyFields(entityClass);
//    } else {
//      UniqueConstraint[] uc = tableAnn.uniqueConstraints();
//      if (uc == null || uc.length == 0) {
//        // Again, there are no unique constraints, so use the first non-special field
//        keyFields = defaultKeyFields(entityClass);
//      } else {
//        try {
//          String[] keyColumnNames = uc[0].columnNames();
//          keyFields = new Field[keyColumnNames.length];
//          for (int i = 0; i < keyFields.length; i++) {
//            keyFields[i] = entityClass.getField(keyColumnNames[i]);
//          }
//        } catch (SecurityException ex) {
//          throw new RuntimeException(ex);
//        } catch (NoSuchFieldException ex) {
//          throw new RuntimeException(ex);
//        }
//      }
//    }
//  }
  
  
//  private Field[] defaultKeyFields(Class<?> entityClass) {
//    Field[] fields = entityClass.getDeclaredFields();
//    for (Field field : fields) {
//      int modifiers = field.getModifiers();
//      if ((modifiers & (Modifier.STATIC | Modifier.TRANSIENT)) != 0) {
//        // Static and transient fields are not key columns
//        continue;
//      }
//      Id idann = field.getAnnotation(Id.class);
//      if (idann != null) {
//        // Version fields are not key columns
//        continue;
//      }
//      Version vann = field.getAnnotation(Version.class);
//      if (vann != null) {
//        // Version fields are not key columns
//        continue;
//      }
//      GeneratedValue gvann = field.getAnnotation(GeneratedValue.class);
//      if (gvann != null) {
//        // Generated fields are not key columns
//        continue;
//      }
//      Transient tann = field.getAnnotation(Transient.class);
//      if (tann != null) {
//        // Transient marked fields are not key columns
//        continue;
//      }
//      return new Field[] {
//        field,
//      };
//    }
//    throw new RuntimeException("No default key field found");
//  }

  
  public String getNextValSql () {
    if (nextValSql == null) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("select nextval('");
      buffer.append(tableName);
      buffer.append('_');
      buffer.append(idPlan.getName());
      buffer.append("_seq')");
      nextValSql = buffer.toString();
    }
    return nextValSql;
  }
  
  
  public String getUpdateFieldSql (String fieldName) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("update ");
    buffer.append(tableName);
    
    String separator = " set ";
    if (versionPlan != null) {
      buffer.append(separator);
      String columnName = toSQLName(versionPlan.getName());
      buffer.append(columnName);
      buffer.append("=?");
      separator = ",";
    }
    buffer.append(separator);
    String columnName = toSQLName(fieldName);
    buffer.append(columnName);
    buffer.append("=?");
    
    buffer.append(" where ");
    buffer.append(idPlan.getName());
    buffer.append("=?");
    return buffer.toString();
  }
  
  
  private int appendAllColumns (StringBuilder buffer) {
    buffer.append(toSQLName(idPlan.getName()));
    int n = 1;
    if (versionPlan != null) {
      buffer.append(',');
      buffer.append(toSQLName(versionPlan.getName()));
      n++;
    }
    n += buildColumnNames(buffer);
    if (entityLifePlan != null) {
      buffer.append(',');
      buffer.append(toSQLName(entityLifePlan.getName()));
      n++;
    }
    return n;
  }
  
  
  public static String getInsertEntitySql (String schema, IEntityPlan<?> entityPlan) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("INSERT INTO ");
    
    String tableName = entityPlan.getName();
    if (schema != null) {
      tableName = schema + '.' + tableName;
    }
    buffer.append(tableName);
    buffer.append('(');
    int[] n = new int[1];
    
    IItemPlan<?> versionPlan = entityPlan.getVersionPlan();
    if (versionPlan != null) {
      buffer.append(',');
      buffer.append(versionPlan.getName());
      n[0]++;
    }
    
    IItemPlan<?> entityLifePlan = entityPlan.getEntityLifePlan();
    if (entityLifePlan != null) {
      buffer.append(',');
      buffer.append(entityLifePlan.getName());
      n[0]++;
    }
    
    List<INodePlan> dataPlans = entityPlan.getDataPlans();
    addDataColumnNames(buffer, dataPlans, "", n);
    buffer.append(") VALUES (");
    appendColumnPlaces (buffer, n[0]);
    buffer.append(')');
    return buffer.toString();
  }
  

  public static String getInsertElementSql (String tableName, INodePlan elementPlan) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("INSERT INTO ");
    buffer.append(tableName);
    buffer.append('(');
    int[] n = new int[1];
    
    buffer.append("parent_id");
    n[0]++;
    
    addDataColumnName (buffer, elementPlan, "", n);
    buffer.append(") VALUES (");
    appendColumnPlaces (buffer, n[0]);
    buffer.append(')');
    return buffer.toString();
  }
  

  private static void addDataColumnName (StringBuilder buffer, INodePlan dataPlan, String prefix, int[] n) {
    switch (dataPlan.getStructure()) {
    case ARRAY :
    case LIST :
      break;
    case EMBEDDED :
      IEmbeddedPlan<?> embeddedPlan = (IEmbeddedPlan<?>)dataPlan;
      addDataColumnNames(buffer, embeddedPlan.getMembers(), prefix + embeddedPlan.getName() + '_', n);
      break;
    case ENTITY :
      throw new IllegalArgumentException("Entity cannot be a child of any node");
    case ITEM :
      if (n[0] > 0) {
        buffer.append(',');
      }
      buffer.append(dataPlan.getName());
      n[0]++;
      break;
    case REFERENCE :
      if (n[0] > 0) {
        buffer.append(',');
      }
      buffer.append(dataPlan.getName());
      n[0]++;
      break;
    default :
      throw new NotYetImplementedException();
    }
  }

  
  private static void addDataColumnNames (StringBuilder buffer, List<INodePlan> dataPlans, String prefix, int[] n) {
    for (INodePlan dataPlan : dataPlans) {
      addDataColumnName(buffer, dataPlan, prefix, n);
    }
  }
  
  
  private static void addDataColumnNames (StringBuilder buffer, INodePlan[] dataPlans, String prefix, int[] n) {
    for (INodePlan dataPlan : dataPlans) {
      addDataColumnName(buffer, dataPlan, prefix, n);
    }
  }
  
  
  private static void appendColumnPlaces (StringBuilder buffer, int n) {
    buffer.append('?');
    for (int i = 1; i < n; i++) {
      buffer.append(',');
      buffer.append('?');
    }
  }

  
  public String getDeleteSql () {
    if (deleteSql == null) {
      IItemPlan<?> idPlan = entityPlan.getIdPlan();
      
      StringBuilder buffer = new StringBuilder();
      buffer.append("delete from ");
      buffer.append(tableName);
      buffer.append(" where ");
      buffer.append(idPlan.getName());
      buffer.append("=?");
      deleteSql = buffer.toString();
    }
    return deleteSql;
  }
  
  
  public String getFetchAllSql () {
    if (fetchAllSql == null) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("select ");
      appendAllColumns (buffer);
      buffer.append(" from ");
      buffer.append(tableName);

      logger.info ("SQL statement {}", buffer);
      fetchAllSql = buffer.toString();
    }
    return fetchAllSql;
  }
  
  
  private static void appendWhereId (StringBuilder buffer, IItemPlan<?> idPlan) {
    buffer.append(" WHERE ");
    buffer.append(toSQLName(idPlan.getName()));
    buffer.append("=?"); 
  }


  private static void appendWhereParentId (StringBuilder buffer) {
    buffer.append(" WHERE ");
    buffer.append("parentId");
    buffer.append("=?"); 
  }


  public static String getFetchByIdSql (String schema, IEntityPlan<?> entityPlan) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("select ");
    
    int[] n = new int[1];
    List<INodePlan> dataPlans = entityPlan.getDataPlans();
    addDataColumnNames(buffer, dataPlans, "", n);
    buffer.append(" from ");
    String tableName = entityPlan.getName();
    if (schema != null) {
      tableName = schema + '.' + tableName;
    }
    buffer.append(tableName);
    
    appendWhereId (buffer, entityPlan.getIdPlan());
    return buffer.toString();
  }

  
  public static String getFetchElementSql (String tableName, INodePlan nodePlan) {
    StringBuilder buffer = new StringBuilder();
    buffer.append("select ");
    
    int[] n = new int[1];

    buffer.append("id");
    n[0]++;
    addDataColumnName(buffer, nodePlan, "", n);
    buffer.append(" from ");
    buffer.append(tableName);
    
    appendWhereParentId (buffer);
    return buffer.toString();
  }

  
  public String getQueryUniqueSql (int n) {
    if (queryUniqueSql[n] == null) {
      IItemPlan<?>[] uniquePlans = entityPlan.getUniqueConstraints().get(n);
      
      StringBuilder buffer = new StringBuilder();
      buffer.append("select ");
      buffer.append(toSQLName(idPlan.getName()));

      buffer.append(" from ");
      buffer.append(tableName);
      
      buffer.append(" where ");
      for (IItemPlan<?> plan : uniquePlans) {
        buffer.append(toSQLName(plan.getName()));
        buffer.append("=? and ");
      }
      buffer.append(toSQLName(idPlan.getName()));
      buffer.append("<>?");
      
      queryUniqueSql[n] = buffer.toString();
    }
    return queryUniqueSql[n];
  }

  
  private void appendDescriptionNames (StringBuilder buffer) {
    int i = 0;
    List<IItemPlan<?>> fieldPlans = entityPlan.getDescriptionPlans();
    for (IItemPlan<?> fieldPlan : fieldPlans) {
      if (i > 0) {
        buffer.append(",");
      }
      buffer.append(toSQLName(fieldPlan.getName()));
      i++;
    }
  }

  
  public String getFetchDescriptionByIdSql () {
    if (fetchDescriptionByIdSql == null) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("select ");
      appendDescriptionNames(buffer);

      buffer.append(" from ");
      buffer.append(tableName);
      
      appendWhereId (buffer, idPlan);
      fetchDescriptionByIdSql = buffer.toString();
    }
    return fetchDescriptionByIdSql;
  }
  
  
  public String getFetchDescriptionAllSql () {
    if (fetchDescriptionAllSql == null) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("select ");
      buffer.append(toSQLName(idPlan.getName()));
      buffer.append(",");
      appendDescriptionNames(buffer);
      if (entityLifePlan != null) {
        buffer.append(",");
        buffer.append(toSQLName(entityLifePlan.getName()));
      }
      buffer.append(" from ");
      buffer.append(tableName);

      fetchDescriptionAllSql = buffer.toString();
    }
    return fetchDescriptionAllSql;
  }
  
  
//  public String getFetchByNaturalKeySql () {
//    if (fetchByNaturalKeySql == null) {
//      StringBuilder buffer = new StringBuilder();
//      buffer.append("select ");
//      buffer.append(toSQLName(idPlan.getName()));
//      if (versionPlan != null) {
//        buffer.append(',');
//        buffer.append(toSQLName(versionPlan.getName()));
//      }
//      for (Field field : dataFields) {
//        buffer.append(',');
//        Class<?> type = field.getType();
//        if (IValueRange.class.isAssignableFrom(type)) {
//          addRangeColumns(buffer, type);
//        } else {
//          String fieldName = toSQLName(field.getName());
//          buffer.append(fieldName);
//        }
//      }
//      
//      buffer.append(" from ");
//      buffer.append(tableName);
//      
//      List<Integer> mappingList = new ArrayList<Integer>();
//      String separator = " where ";
//      int i = 0;
//      for (Field field : keyFields) {
//        buffer.append(separator);
//        Class<?> type = field.getType();
//        if (IValueRange.class.isAssignableFrom(type)) {
//          addRangeCondition(buffer, type);
//          mappingList.add(i);
//          mappingList.add(i);
//          mappingRequired = true;
//        } else {
//          String fieldName = toSQLName(field.getName());
//          buffer.append(fieldName);
//          buffer.append("=?");
//          mappingList.add(i);
//        }
//        i++;
//        separator = " and ";
//      }
//      if (mappingRequired) {
//        mapping = new int[mappingList.size()];
//        for (int j = 0; j < mapping.length; j++) {
//          mapping[j] = mappingList.get(j);
//        }
//      }
//      fetchByNaturalKeySql = buffer.toString();
//    }
//    return fetchByNaturalKeySql;
//  }
//  
//  
//  public String getQueryByNaturalKeySql () {
//    if (queryByNaturalKeySql == null) {
//      StringBuilder buffer = new StringBuilder();
//      buffer.append("select ");
//      buffer.append(toSQLName(idPlan.getName()));
//      if (versionPlan != null) {
//        buffer.append(',');
//        buffer.append(toSQLName(versionPlan.getName()));
//      }
//      
//      buffer.append(" from ");
//      buffer.append(tableName);
//      
//      List<Integer> mappingList = new ArrayList<Integer>();
//      String separator = " where ";
//      int i = 0;
//      for (Field field : keyFields) {
//        buffer.append(separator);
//        Class<?> type = field.getType();
//        if (IValueRange.class.isAssignableFrom(type)) {
//          addRangeCondition(buffer, type);
//          mappingList.add(i);
//          mappingList.add(i);
//          mappingRequired = true;
//        } else {
//          String fieldName = toSQLName(field.getName());
//          buffer.append(fieldName);
//          buffer.append("=?");
//          mappingList.add(i);
//        }
//        i++;
//        separator = " and ";
//      }
//      if (mappingRequired) {
//        mapping = new int[mappingList.size()];
//        for (int j = 0; j < mapping.length; j++) {
//          mapping[j] = mappingList.get(j);
//        }
//      }
//      queryByNaturalKeySql = buffer.toString();
//    }
//    return queryByNaturalKeySql;
//  }
  
  
//  private void addRangeColumns (StringBuilder buffer, Class<?> rangeClass) {
//    int i = 0;
//    Field[] fx = rangeClass.getDeclaredFields();
//    for (Field field : fx) {
//      int m = field.getModifiers();
//      if ((m & (Modifier.STATIC | Modifier.TRANSIENT)) == 0) {
//        switch (i) {
//        case 0 :
//          String fieldName1 = toSQLName(field.getName());
//          buffer.append(fieldName1);
//          i++;
//          break;
//        case 1 :
//          buffer.append(",");
//          String fieldName2 = toSQLName(field.getName());
//          buffer.append(fieldName2);
//          i++;
//          break;
//        }
//      }
//    }
//  }
  
  
//  private void addRangeCondition (StringBuilder buffer, Class<?> rangeClass) {
//    int i = 0;
//    Field[] fx = rangeClass.getDeclaredFields();
//    for (Field field : fx) {
//      int m = field.getModifiers();
//      if ((m & (Modifier.STATIC | Modifier.TRANSIENT)) == 0) {
//        switch (i) {
//        case 0 :
//          buffer.append("(");
//          String fieldName1 = toSQLName(field.getName());
//          buffer.append(fieldName1);
//          i++;
//          break;
//        case 1 :
//          buffer.append("<=? and ?<");
//          String fieldName2 = toSQLName(field.getName());
//          buffer.append(fieldName2);
//          buffer.append(")");
//          i++;
//          break;
//        }
//      }
//    }
//  }
  
  
//  public String getKeyUpdateSql () {
//    if (keyUpdateSql == null) {
//      StringBuilder buffer = new StringBuilder();
//      buffer.append("update ");
//      buffer.append(tableName);
//      
//      buildUpdateSet(buffer, " set ", keyFields);
//      buffer.append(" where ");
//      buffer.append(idPlan.getName());
//      buffer.append("=?");
//      keyUpdateSql = buffer.toString();
//    }
//    return keyUpdateSql;
//  }

  
  public String getDataUpdateSql () {
    if (dataUpdateSql == null) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("update ");
      buffer.append(tableName);
      
      buildUpdateSet(buffer, " set ", dataPlans);
      buffer.append(" where ");
      buffer.append(idPlan.getName());
      buffer.append("=?");
      dataUpdateSql = buffer.toString();
    }
    return dataUpdateSql;
  }
  

  public String getLifeUpdateSql () {
    if (lifeUpdateSql == null) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("update ");
      buffer.append(tableName);
      
      buildUpdateSet(buffer, " set ", new IItemPlan<?>[] {entityLifePlan});
      buffer.append(" where ");
      buffer.append(idPlan.getName());
      buffer.append("=?");
      lifeUpdateSql = buffer.toString();
    }
    return lifeUpdateSql;
  }
  

  private void buildUpdateSet (StringBuilder buffer, String separator, IItemPlan<?>[] plans) {
    if (versionPlan != null) {
      buffer.append(separator);
      String columnName = toSQLName(versionPlan.getName());
      buffer.append(columnName);
      buffer.append("=?");
      separator = ",";
    }
    for (IItemPlan<?> plan : plans) {
      buffer.append(separator);
      String columnName = toSQLName(plan.getName());
      buffer.append(columnName);
      buffer.append("=?");
      separator = ",";
    }
  }

  
  public String getDeleteAllSql () {
    if (deleteAllSql == null) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("delete from ");
      buffer.append(tableName);
      deleteAllSql = buffer.toString();
    }
    return deleteAllSql;
  }

  
  public int[] getMapping() {
    if (mappingRequired) {
      return mapping;
    } else {
      return null;
    }
  }


  public String getEntityName (boolean upperCase) {
    String name = CamelCase.toSentence(entityPlan.getName());
    if (!upperCase) {
      name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
    return name;
  }

}
