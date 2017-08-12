package org.gyfor.dao.mapping;

import java.util.List;
import java.util.Stack;

import org.gyfor.object.plan.IEmbeddedPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.object.plan.PlanStructure;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.todo.NotYetImplementedException;

public class RowAdder<T> extends TableManipulation {

  private final IConnection conn;
  
  private final String schema;
  
  private final IEntityPlan<T> entityPlan;
  
  private class ElementRow {
    private final String parentTableName;
    
    private final PlanStructure parentStructure;
    
    private final INodePlan elementPlan; 
    
    private final int dimension;
 
    private final Object value;
    
    
    private ElementRow (String parentTableName, PlanStructure parentStructure, INodePlan elementPlan, int dimension, Object value) {
      this.parentTableName = parentTableName;
      this.parentStructure = parentStructure;
      this.dimension = dimension;
      this.elementPlan = elementPlan;
      this.value = value;
    }
    
    public void addElementRows (Stack<ElementRow> queuedElementRows) {
      String tableName = buildTableName(parentTableName, elementPlan, dimension);
      String insertElementSql = SQLBuilder.getInsertElementSql(tableName, elementPlan);
      
      try (IPreparedStatement stmt = conn.prepareStatement(insertElementSql)) 
      {
        switch (parentStructure) {
        case ARRAY :
          Object[] array = (Object[])value;
          for (Object elementValue : array) {
            setNodeValue(stmt, tableName, elementPlan, elementValue, queuedElementRows);
            stmt.executeUpdate();          
          }
          break;
        case LIST :
          @SuppressWarnings("unchecked")
          List<Object> list = (List<Object>)value;
          for (Object elementValue : list) {
            setNodeValue(stmt, tableName, elementPlan, elementValue, queuedElementRows);
            stmt.executeUpdate();          
          }
          break;
        default :
          throw new RuntimeException("This should not happen");
        }
      }
    }

  }
  
  
  public RowAdder (IConnection conn, String schema, IEntityPlan<T> entityPlan) {
    this.conn = conn;
    this.schema = schema;
    this.entityPlan = entityPlan;
  }
  
  
  private void setNodeValue (IPreparedStatement stmt, String parentTableName, INodePlan nodePlan, Object value, Stack<ElementRow> queuedRepeatingModels) {
    switch (nodePlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingPlan repeatingPlan = (IRepeatingPlan)nodePlan;
      ElementRow elementRow = new ElementRow(parentTableName, nodePlan.getStructure(), repeatingPlan.getElementPlan(), repeatingPlan.getDimension(), value);
      queuedRepeatingModels.add(elementRow);
      break;
    case EMBEDDED :
      IEmbeddedPlan<?> embeddedPlan = (IEmbeddedPlan<?>)nodePlan;
      setNodeValues (stmt, parentTableName, embeddedPlan.getMembers(), value, queuedRepeatingModels);
      break;
    case ENTITY :
      throw new IllegalArgumentException("Entity model cannot be the child of any other model");
    case ITEM :
      @SuppressWarnings("unchecked") 
      IItemPlan<Object> itemPlan = (IItemPlan<Object>)nodePlan;
      itemPlan.setStatementFromValue(stmt, value);
      break;
    case REFERENCE :
      @SuppressWarnings("unchecked") 
      IReferencePlan<Integer> referencePlan = (IReferencePlan<Integer>)nodePlan;
      Integer idOfReferencedEntity = (Integer)value;
      referencePlan.setStatementFromValue(stmt, idOfReferencedEntity);
      break;
    default :
      throw new NotYetImplementedException();
    }
  }
  
  
  private void setNodeValues (IPreparedStatement stmt, String parentTableName, List<INodePlan> nodePlans, Object value, Stack<ElementRow> queuedElementRows) {
    for (INodePlan nodePlan : nodePlans) {
      setNodeValue(stmt, parentTableName, nodePlan, nodePlan.getFieldValue(value), queuedElementRows);
    }
  }

  
  private void setNodeValues (IPreparedStatement stmt, String parentTableName, INodePlan[] nodePlans, Object value, Stack<ElementRow> queuedElementRows) {
    for (INodePlan nodePlan : nodePlans) {
      setNodeValue(stmt, parentTableName, nodePlan, nodePlan.getFieldValue(value), queuedElementRows);
    }
  }

  
  private void setEntityValues (IPreparedStatement stmt, String parentTableName, IEntityPlan<?> entityPlan, Object value, Stack<ElementRow> queuedElementRows) {
    setNodeValues (stmt, parentTableName, entityPlan.getDataPlans(), value, queuedElementRows);
  }

  
  public void addEntityRow (T value) {
    String addRowSql = SQLBuilder.getInsertEntitySql(schema, entityPlan);
    System.out.println(addRowSql);
    
    IPreparedStatement stmt = conn.prepareStatement(addRowSql);
    Stack<ElementRow> queuedElementRows = new Stack<>();
    setEntityValues (stmt, entityPlan.getName(), entityPlan, value, queuedElementRows);
    stmt.executeUpdate();
    
    while (queuedElementRows.size() > 0) {
      ElementRow elementRow = queuedElementRows.pop();
      elementRow.addElementRows(queuedElementRows);
    }
  }

}
