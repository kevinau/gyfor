package org.gyfor.dao.sql;

import java.util.Arrays;
import java.util.List;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.util.CamelCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SQLBuilder {

  public static class Expression {
    private StringBuilder buffer = new StringBuilder();
    private String sql;
    private IItemPlan<?>[] sqlPlans = new IItemPlan<?>[0];
    
    public void setSql(StringBuilder buffer) {
      this.sql = sql.toString();
    }
    
    public void append(String x) {
      buffer.append(x);
    }
    
    public void append(char x) {
      buffer.append(x);
    }
    
    public void appendParam(int i) {
      if (i > 0) {
        buffer.append(',');
      }
      buffer.append('?');
    }
    
    public void appendInput(IItemPlan<?> plan) {
      String columnName = toSQLName(plan.getName());
      buffer.append(columnName);
    }
    
    public void appendOutput(IItemPlan<?> plan) {
      String columnName = toSQLName(plan.getName());
      buffer.append(columnName);

      int n = sqlPlans.length;
      sqlPlans = Arrays.copyOf(sqlPlans, n + 1);
      sqlPlans[n] = plan;
    }
    
    public void appendOutput(int i, IItemPlan<?> plan) {
      if (i > 0) {
        buffer.append(',');
      }
      appendOutput(plan);
    }
        
    public String sql() {
      if (sql == null) {
        sql = buffer.toString();
      }
      return sql;
    }
    
    public IItemPlan<?>[] sqlPlans() {
      return sqlPlans;
    }
    
    @Override
    public String toString() {
      return sql;
    }
  }
  
  
  private IEntityPlan<?> entityPlan;
  
  private String tableName;
  private IItemPlan<?> idPlan;
  private IItemPlan<?> versionPlan;
  //private IItemPlan<?>[] keyFields;
  private IItemPlan<?>[] dataPlans;
  private IItemPlan<?> entityLifePlan;
  
  private Expression nextValSql = null;
  private Expression insertSql = null;
  private Expression deleteSql = null;
  private Expression deleteAllSql = null;
  //private String keyUpdateSql = null;
  private Expression dataUpdateSql = null;
  private Expression lifeUpdateSql = null;
  private Expression fetchAllSql = null;
  private Expression fetchByIdSql = null;
  private Expression fetchDescriptionByIdSql = null;
  private Expression fetchDescriptionAllSql = null;
  private Expression[] queryUniqueSql = null;
  //private String fetchByNaturalKeySql = null;
  //private String queryByNaturalKeySql = null;
  
  private boolean mappingRequired = false;
  private int[] mapping;

  
  private Logger logger = LoggerFactory.getLogger(SQLBuilder.class);
  
  
  public SQLBuilder (IEntityPlan<?> entityPlan, String schemaName) {
    this.entityPlan = entityPlan;

    tableName = toSQLName(entityPlan.getEntityName());
    if (schemaName != null) {
      tableName = schemaName + "." + tableName;
    }
    
    //buildKeyFields();
    
    idPlan = entityPlan.getIdPlan();
    versionPlan = entityPlan.getVersionPlan();
    List<INodePlan> dx = entityPlan.getDataPlans();
    dataPlans = dx.toArray(new IItemPlan<?>[0]);
    entityLifePlan = entityPlan.getEntityLifePlan();
    
    int n = entityPlan.getUniqueConstraints().size();
    queryUniqueSql = new Expression[n];
  }


  private static String toSQLName (String name) {
//    StringBuffer buffer = new StringBuffer();
//    int n = name.length();
//    
//    int i = 0;
//    boolean lastUppercase = false;
//    while (i < n) {
//      char c = name.charAt(i);
//      if (lastUppercase && !Character.isUpperCase(c)) {
//        if (i > 1) {
//          buffer.insert(i - 1, '_');
//        }
//      }
//      buffer.append(c);
//      lastUppercase = Character.isUpperCase(c);
//      i++;
//    }
//    return buffer.toString();
    return name;
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

  
  public Expression getNextValSql () {
    if (nextValSql == null) {
      Expression sql = new Expression();
      sql.append("select nextval('");
      sql.append(tableName);
      sql.append('_');
      sql.append(idPlan.getName());
      sql.append("_seq')");
      nextValSql = sql;
    }
    return nextValSql;
  }
  
  
  private int appendAllColumns (Expression sql) {
    sql.appendInput(idPlan);
    int n = 1;
    if (versionPlan != null) {
      sql.append(',');
      sql.appendInput(versionPlan);
      n++;
    }
    n += buildColumnNames(sql);
    if (entityLifePlan != null) {
      sql.append(',');
      sql.appendInput(entityLifePlan);
      n++;
    }
    return n;
  }
  
  
  public Expression getInsertSql () {
    if (insertSql == null) {
      Expression sql = new Expression();
      sql.append("insert into ");
      sql.append(tableName);
      sql.append('(');
      int n = appendAllColumns (sql);
      sql.append(") values (");
      appendColumnPlaces(sql, n);
      sql.append(")");
      insertSql = sql;
    }
    return insertSql;
  }
  

  private int buildColumnNames (Expression sql) {
    int n = 0;
    for (IItemPlan<?> dataPlan : dataPlans) {
      // TODO support ranges and other fields that require multiple database columns
//      Class<?> type = field.getType();
//      if (IValueRange.class.isAssignableFrom(type)) {
//        buildColumnNames (buffer, type.getDeclaredFields());
//      } else {
        sql.append(',');
        sql.appendInput(dataPlan);
//      }
      n++;
    }
    return n;
  }
  
  
  private void appendColumnPlaces (Expression sql, int n) {
    sql.append('?');
    for (int i = 1; i < n; i++) {
      sql.append(',');
      sql.append('?');
    }
  }

  
  public Expression getDeleteSql () {
    if (deleteSql == null) {
      IItemPlan<?> idPlan = entityPlan.getIdPlan();
      
      Expression sql = new Expression();
      sql.append("delete from ");
      sql.append(tableName);
      sql.append(" where ");
      sql.appendInput(idPlan);
      sql.append("=?");
      deleteSql = sql;
    }
    return deleteSql;
  }
  
  
  public Expression getFetchAllSql () {
    if (fetchAllSql == null) {
      Expression sql = new Expression();
      sql.append("select ");
      appendAllColumns(sql);
      sql.append(" from ");
      sql.append(tableName);

      logger.info ("SQL statement {}", sql);
      fetchAllSql = sql;
    }
    return fetchAllSql;
  }
  
  
  private void appendWhereId (Expression sql) {
    sql.append(" where ");
    sql.appendInput(idPlan);
    sql.append("=?"); 
}


  public Expression getFetchByIdSql () {
    if (fetchByIdSql == null) {
      Expression sql = new Expression();
      sql.append("select ");
      appendAllColumns(sql);

      sql.append(" from ");
      sql.append(tableName);
      
      appendWhereId (sql);
      fetchByIdSql = sql;
    }
    return fetchByIdSql;
  }

  
  public Expression getQueryUniqueSql (int n) {
    if (queryUniqueSql[n] == null) {
      IItemPlan<?>[] uniquePlans = entityPlan.getUniqueConstraints().get(n);
      
      Expression sql = new Expression();
      sql.append("select ");
      sql.appendOutput(idPlan);

      sql.append(" from ");
      sql.append(tableName);
      
      sql.append(" where ");
      for (IItemPlan<?> plan : uniquePlans) {
        sql.appendInput(plan);
        sql.append("=? and ");
      }
      sql.appendInput(idPlan);
      sql.append("<>?");
      
      queryUniqueSql[n] = sql;
    }
    return queryUniqueSql[n];
  }

  
  private void appendDescriptionNames (int i, Expression sql) {
    List<IItemPlan<?>> itemPlans = entityPlan.getDescriptionPlans();
    for (IItemPlan<?> itemPlan : itemPlans) {
      sql.appendOutput(i, itemPlan);
      i++;
    }
  }

  
  public Expression getFetchDescriptionByIdSql () {
    if (fetchDescriptionByIdSql == null) {
      Expression sql = new Expression();
      sql.append("select ");
      appendDescriptionNames(0, sql);

      sql.append(" from ");
      sql.append(tableName);
      
      appendWhereId(sql);
      fetchDescriptionByIdSql = sql;
    }
    return fetchDescriptionByIdSql;
  }
  
  
  public Expression getFetchDescriptionAllSql () {
    if (fetchDescriptionAllSql == null) {
      Expression sql = new Expression();
      sql.append("select");
      sql.appendOutput(idPlan);
      sql.append(',');
      appendDescriptionNames(1, sql);
      if (entityLifePlan != null) {
        sql.append(',');
        sql.appendOutput(entityLifePlan);
      }
      sql.append(" from ");
      sql.append(tableName);

      fetchDescriptionAllSql = sql;
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

  
  public Expression getDataUpdateSql () {
    if (dataUpdateSql == null) {
      Expression sql = new Expression();
      sql.append("update ");
      sql.append(tableName);
      
      buildUpdateSet(sql, " set ", dataPlans);
      sql.append(" where ");
      sql.append(idPlan.getName());
      sql.append("=?");
      dataUpdateSql = sql;
    }
    return dataUpdateSql;
  }
  

  public Expression getLifeUpdateSql () {
    if (lifeUpdateSql == null) {
      Expression sql = new Expression();
      sql.append("update ");
      sql.append(tableName);
      
      buildUpdateSet(sql, " set ", new IItemPlan<?>[] {entityLifePlan});
      sql.append(" where ");
      sql.appendInput(idPlan);
      sql.append("=?");
      lifeUpdateSql = sql;
    }
    return lifeUpdateSql;
  }
  

  private void buildUpdateSet (Expression sql, String separator, IItemPlan<?>[] plans) {
    if (versionPlan != null) {
      sql.append(separator);
      sql.appendInput(versionPlan);
      sql.append("=?");
      separator = ",";
    }
    for (IItemPlan<?> plan : plans) {
      sql.append(separator);
      sql.appendInput(plan);
      sql.append("=?");
      separator = ",";
    }
  }

  
  public Expression getDeleteAllSql () {
    if (deleteAllSql == null) {
      Expression sql = new Expression();
      sql.append("delete from ");
      sql.append(tableName);
      deleteAllSql = sql;
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
