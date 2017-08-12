package org.gyfor.dao.mapping;

import java.util.List;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INameMappedPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.sql.RowNotFoundException;
import org.gyfor.todo.NotYetImplementedException;

public class RowFetcher<T> extends TableManipulation {

  private final IConnection conn;
  private final String schema;
  private final IEntityPlan<T> entityPlan;
  
  
  public RowFetcher (IConnection conn, String schema, IEntityPlan<T> entityPlan) {
    this.conn = conn;
    this.schema = schema;
    this.entityPlan = entityPlan;
  }
  
  
  public void fetchElementRows (String parentTableName, INodePlan elementPlan, int dimension, int parentId, Object instance) {
    String tableName = buildTableName(parentTableName, elementPlan, dimension);
    String fetchElementSql = SQLBuilder.getFetchElementSql(tableName, elementPlan);
    
    IPreparedStatement stmt = conn.prepareStatement(fetchElementSql);
    stmt.setInt(parentId);
    IResultSet rs = stmt.executeQuery();
    while (rs.next()) {
      int[] parentId2 = new int[1];
      Object elementValue = elementPlan.newInstance(fromValue);
      getNodeValue(rs, parentTableName, repeatingModel, parentId2);
    }
    rs.close();
  }

  
  private void getNodeValue (IResultSet rs, String parentTableName, INodePlan nodePlan, Object instance, int[] parentId) {
    switch (nodePlan.getStructure()) {
    case ARRAY :
      IRepeatingPlan arrayPlan = (IRepeatingPlan)nodePlan;
      fetchElementRows(parentTableName, arrayPlan.getElementPlan(), arrayPlan.getDimension(), parentId[0], arrayPlan);
      break;
    case LIST :
      IRepeatingPlan listPlan = (IRepeatingPlan)nodePlan;
      fetchElementRows(parentTableName, listPlan.getElementPlan(), listPlan.getDimension(), parentId[0], listPlan);
      break;
    case EMBEDDED :
      INameMappedPlan<?> embeddedPlan = (INameMappedPlan<?>)nodePlan;
      getNodeValues (rs, parentTableName, embeddedPlan.getMembers(), instance, parentId);
      break;
    case ENTITY :
      throw new IllegalArgumentException("Entity model cannot be the child of any other model");
    case ITEM :
      IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
      if (itemPlan.isId()) {
        IItemPlan<Integer> idPlan = (IItemPlan<Integer>)itemPlan;
        parentId[0] = idPlan.getResultValue(rs);
        itemPlan.setFieldValue(instance, parentId[0]);
      } else {
        Object value = itemPlan.getResultValue(rs);
        itemPlan.setFieldValue(instance, value);
      }
      break;
    case REFERENCE :
      IReferencePlan<Integer> referencePlan = (IReferencePlan<Integer>)nodePlan;
      Integer referencedId = referencePlan.getResultValue(rs);
      referencePlan.setFieldValue(instance, referencedId);
      break;
    default :
      throw new NotYetImplementedException();
    }
  }
  
  
  private void getNodeValues (IResultSet rs, String parentTableName, List<INodePlan> nodePlans, Object instance, int[] parentId) {
    for (INodePlan nodePlan : nodePlans) {
      Object nodeValue = nodePlan.getFieldValue(instance);
      getNodeValue(rs, parentTableName, nodePlan, nodeValue, parentId);
    }
  }

  
  private void getNodeValues (IResultSet rs, String parentTableName, INodePlan[] nodePlans, Object instance, int[] parentId) {
    for (INodePlan nodePlan : nodePlans) {
      Object nodeValue = nodePlan.getFieldValue(instance);
      getNodeValue(rs, parentTableName, nodePlan, nodeValue, parentId);
    }
  }

  
  private void getEntityValues (IResultSet rs, String parentTableName, IEntityPlan<?> entityPlan, T instance, int[] parentId) {
    getNodeValues (rs, parentTableName, entityPlan.getDataPlans(), instance, parentId);
  }

  
  public T fetchRowById (int id) throws RowNotFoundException {
    String fetchRowSql = SQLBuilder.getFetchByIdSql(schema, entityPlan);
    System.out.println(fetchRowSql);
    
    try (
      IPreparedStatement stmt = conn.prepareStatement(fetchRowSql);
      IResultSet rs = stmt.executeQuery(id))
    {
      if (rs.next()) {
        T value = entityPlan.newInstance();
        int[] parentId = new int[1];
        getEntityValues(rs, entityPlan.getName(), entityPlan, value, parentId);
        return value;
      } else {
        throw new RowNotFoundException(entityPlan.getName(), id);
      }
    }
  }

}
