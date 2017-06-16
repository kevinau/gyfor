package org.gyfor.dao.sql;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IConnectionFactory;
import org.gyfor.sql.IPreparedStatement;


public class EntityDAO {

  public void dropTable (IConnection conn, IEntityPlan<?> plan) {
    // TODO Drop any related records
    StringBuilder sql = new StringBuilder();
    sql.append("DROP SEQUENCE ");
    appendTableName(sql, plan);
    sql.append('_');
    appendNodeName(sql, plan.getIdPlan());
    sql.append("_seq CASCADE");
    IPreparedStatement stmt = conn.prepareStatement(sql);
    stmt.executeUpdate();
    
    sql.setLength(0);
    sql.append("DROP TABLE");
    appendTableName(sql, plan);
    sql.append(" CASCADE");
    stmt = conn.prepareStatement(sql);
    stmt.executeUpdate();
  }
  
  
  public void dropTableCommit (IConnectionFactory connFactory, IEntityPlan<?> plan) {
    try (IConnection conn = connFactory.getIConnection()) {
      conn.setAutoCommit(false);
      dropTable(conn, plan);
      conn.commit();
    }
  }
  
  
  public void createTable (IEntityPlan<?> plan) {
    // Start transaction
    // Drop table, ignoring table does not exist error
    // Build create table sql statement
    // Execute the sql statement
    // Create any related records (such as array/list elements and optional embedded models)
    // Commit transaction
  }
  
  public void addRow (IEntityModel model) {
    // Start transaction
    // Build add sql statement
    // Add the record, and get back the entity id
    // Build and add any related records (such as array/list elements and optional embedded models)
    // Commit transaction
  }

}
