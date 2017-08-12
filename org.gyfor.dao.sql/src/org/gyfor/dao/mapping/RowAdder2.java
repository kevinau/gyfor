package org.gyfor.dao.mapping;

import java.util.List;
import java.util.Stack;

import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.todo.NotYetImplementedException;

public class RowAdder2 extends TableManipulation {

  private final IConnection conn;
  
  private class ElementRow {
    private final String parentTableName;
    
    private final INodePlan elementPlan; 
    
    private final int dimension;
    
    private final IRepeatingModel repeatingModel;
    
    
    private ElementRow (String parentTableName, INodePlan elementPlan, int dimension, IRepeatingModel repeatingModel) {
      this.parentTableName = parentTableName;
      this.dimension = dimension;
      this.elementPlan = elementPlan;
      this.repeatingModel = repeatingModel;
    }
    
    public void addElementRows (Stack<ElementRow> queuedElementRows) {
      String tableName = buildTableName(parentTableName, elementPlan, dimension);
      String insertElementSql = SQLBuilder.getInsertElementSql(tableName, elementPlan);
      
      IPreparedStatement stmt = conn.prepareStatement(insertElementSql);
      INodeModel[] elementNodes = repeatingModel.getMembers();
      for (INodeModel elementNode : elementNodes) {
        setNodeValue(stmt, tableName, elementNode, queuedElementRows);
        stmt.executeUpdate();
      }
    }

  }
  
  
  public RowAdder2 (IConnection conn) {
    this.conn = conn;
  }
  
  
  private void setNodeValue (IPreparedStatement stmt, String parentTableName, INodeModel nodeModel, Stack<ElementRow> queuedRepeatingModels) {
    INodePlan nodePlan = nodeModel.getPlan();
    switch (nodePlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingModel repeatingModel = (IRepeatingModel)nodeModel;
      IRepeatingPlan repeatingPlan = repeatingModel.getPlan();
      ElementRow elementRow = new ElementRow(parentTableName, repeatingPlan.getElementPlan(), repeatingPlan.getDimension(), repeatingModel);
      queuedRepeatingModels.add(elementRow);
      break;
    case EMBEDDED :
      INameMappedModel embeddedModel = (INameMappedModel)nodeModel;
      setNodeValues (stmt, parentTableName, embeddedModel.getMembers(), queuedRepeatingModels);
      break;
    case ENTITY :
      throw new IllegalArgumentException("Entity model cannot be the child of any other model");
    case ITEM :
      IItemModel itemModel = (IItemModel)nodeModel;
      IItemPlan<Object> itemPlan = itemModel.getPlan();
      Object value = itemModel.getValue();
      itemPlan.setStatementFromValue(stmt, value);
      break;
    case REFERENCE :
      IReferenceModel referenceModel = (IReferenceModel)nodeModel;
      IReferencePlan<Integer> referencePlan = referenceModel.getPlan();
      Integer idOfReferencedEntity = referenceModel.getValue();
      referencePlan.setStatementFromValue(stmt, idOfReferencedEntity);
      break;
    default :
      throw new NotYetImplementedException();
    }
  }
  
  
  private void setNodeValues (IPreparedStatement stmt, String parentTableName, List<INodeModel> nodeModels, Stack<ElementRow> queuedElementRows) {
    for (INodeModel nodeModel : nodeModels) {
      setNodeValue(stmt, parentTableName, nodeModel, queuedElementRows);
    }
  }

  
  private void setNodeValues (IPreparedStatement stmt, String parentTableName, INodeModel[] nodeModels, Stack<ElementRow> queuedElementRows) {
    for (INodeModel nodeModel : nodeModels) {
      setNodeValue(stmt, parentTableName, nodeModel, queuedElementRows);
    }
  }

  
  private void setEntityValues (IPreparedStatement stmt, String parentTableName, IEntityModel entityModel, Stack<ElementRow> queuedElementRows) {
    setNodeValues (stmt, parentTableName, entityModel.getDataModels(), queuedElementRows);
  }

  
  public void addEntityRow (String schema, IEntityModel entityModel) {
    String addRowSql = SQLBuilder.getInsertEntitySql(schema, entityModel.getPlan());
    System.out.println(addRowSql);
    
    IPreparedStatement stmt = conn.prepareStatement(addRowSql);
    Stack<ElementRow> queuedElementRows = new Stack<>();
    setEntityValues (stmt, entityModel.getName(), entityModel, queuedElementRows);
    stmt.executeUpdate();
    
    while (queuedElementRows.size() > 0) {
      ElementRow elementRow = queuedElementRows.pop();
      elementRow.addElementRows(queuedElementRows);
    }
  }

  
  public static void main (String[] args) {
    RowAdder rowAdder = new RowAdder(null);
    
    PlanFactory planFactory = new PlanFactory();
    ModelFactory modelFactory = new ModelFactory(planFactory);
    
//    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(EntityWithArrayOfString.class);
//    tableCreator.createEntityTable("public", entityPlan);

    IEntityModel entityModel = modelFactory.buildEntityModel(SimpleEntity.class);
    rowAdder.addEntityRow("public", entityModel);
  }
}
