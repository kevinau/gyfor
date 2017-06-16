package org.gyfor.dao.mapping;

import java.sql.SQLException;
import java.text.MessageFormat;

import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.IEmbeddedPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.dialect.IDialect;
import org.gyfor.todo.NotYetImplementedException;

public class TableDropper extends TableManipulation {

  private final IConnection conn;
  private final IDialect dialect;
  
  
  private void dropTable (String tableName) {
    String sql = MessageFormat.format(dialect.dropTableTemplate(), tableName);
    IPreparedStatement stmt = conn.prepareStatement(sql);
    try {
      stmt.executeUpdateAllowingException();
    } catch (SQLException ex) {
      if (ex.getSQLState().equals(dialect.noTableState())) {
        // We ignore this error
      } else {
        throw new RuntimeException(ex);
      }
    }
  }
  
  
  public void dropElementTable (INodePlan elementPlan, String schema, String parentTableName, int dimension) {
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
      
    switch (elementPlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingPlan elementPlan2 = (IRepeatingPlan)elementPlan;
      dropElementTable(elementPlan2.getElementPlan(), schema, tableName, elementPlan2.getDimension());
      break;
    case EMBEDDED :
      addMemberNodes(schema, tableName, (IEmbeddedPlan<?>)elementPlan);
      break;
    case ENTITY :
      throw new IllegalArgumentException("IEntityPlan cannot be a child of a element");
    case INTERFACE :
      throw new NotYetImplementedException();
    case ITEM :
      // Do nothing
      break;
    case MAP :
      throw new NotYetImplementedException();
    case REFERENCE :
      // Do nothing
      break;
    case SET :
      throw new NotYetImplementedException();
    }
     
    dropTable(tableName);
  }  
  
  
  public TableDropper (IConnection conn) {
    this.conn = conn;
    this.dialect = conn.getDialect();
  }
  
  
  private void addMemberNode (String schema, String parentTableName, INodePlan nodePlan) {
    switch (nodePlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingPlan elementPlan2 = (IRepeatingPlan)nodePlan;
      dropElementTable(elementPlan2.getElementPlan(), schema, parentTableName, elementPlan2.getDimension());
      break;
    case EMBEDDED :
      IEmbeddedPlan<?> embeddedPlan = (IEmbeddedPlan<?>)nodePlan;
      addMemberNodes (schema, parentTableName, embeddedPlan);
      break;
    case ENTITY :
      throw new IllegalArgumentException("IEntityPlan cannot be a child of an entity");
    case INTERFACE :
      throw new NotYetImplementedException();
    case ITEM :
      // Do nothing
      break;
    case MAP :
      throw new NotYetImplementedException();
    case REFERENCE :
      // Do nothing
      break;
    case SET :
      throw new NotYetImplementedException();
    }
  }
  
  
  private void addMemberNodes (String schema, String parentTableName, IClassPlan<?> embeddedPlan) {
    for (INodePlan nodePlan : embeddedPlan.getMembers()) {
      addMemberNode (schema, parentTableName, nodePlan);
    }
  }
  
  
//  public void dropTableSQL (String schema, IEntityPlan<?> entityPlan) {
//    StringBuilder buffer = new StringBuilder();
//  
//    checkMemberNodes(entityPlan, "");
//    buffer.append("DROP TABLE ");
//    String tableName = getTableName(schema, entityPlan);
//    buffer.append(tableName);
//    buffer.append(";" + NL);
//    
//    System.out.println(buffer);
////  IPreparedStatement stmt = conn.prepareStatement(buffer);
////  stmt.executeUpdate();
////  existingTables.add(entityPlan);
//  }

  
  public void dropElementTable (String schema, String parentTableName, INodePlan elementPlan, int dimension) {
    dropTable(parentTableName + "_" + dimension);
    
    switch (elementPlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingPlan elementPlan2 = (IRepeatingPlan)elementPlan;
      dropElementTable(elementPlan2.getElementPlan(), schema, parentTableName, elementPlan2.getDimension());
      //queuedElementTables.add(elementTable);
      break;
    case EMBEDDED :
      addMemberNodes(schema, parentTableName, (IEmbeddedPlan<?>)elementPlan);
      break;
    case ENTITY :
      throw new IllegalArgumentException("IEntityPlan cannot be a child of a element");
    case INTERFACE :
      throw new NotYetImplementedException();
    case ITEM :
      // Do nothing
      break;
    case MAP :
      throw new NotYetImplementedException();
    case REFERENCE :
      // Do nothing
      break;
    case SET :
      throw new NotYetImplementedException();
    }

//  existingTables.add(entityPlan);
  }

  
  public void dropEntityTable (String schema, IEntityPlan<?> entityPlan) {
    String tableName = getTableName(schema, entityPlan);
    addMemberNodes (schema, tableName, entityPlan);
    
    dropTable(tableName);
  }

  
//  public void dropTableCommit (IConnectionFactory connFactory, IEntityPlan<?> entityPlan) {
//    try (IConnection conn = connFactory.getIConnection()) {
//      conn.setAutoCommit(false);
//      dropTable(conn, entityPlan);
//      conn.commit();
//    }
//  }
  
  
  public static void main (String[] args) {
    TableDropper tableDropper = new TableDropper(null);
    PlanFactory planFactory = new PlanFactory();

//    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(EntityWithArrayOfString.class);
//    entityPlan.dump();
//    tableDropper.dropEntityTable("public", entityPlan);

    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(EntityWithArrayArrayOfString.class);
    tableDropper.dropEntityTable("public", entityPlan);
  }
}
