package org.gyfor.dao.mapping;

import java.util.List;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IReferenceModel;
import org.gyfor.object.model.IRepeatingModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.sql.RowNotFoundException;
import org.gyfor.todo.NotYetImplementedException;

public class RowFetcher extends TableManipulation {

  private final IConnection conn;
  private final ModelFactory modelFactory;
  
  
  public RowFetcher (IConnection conn, ModelFactory modelFactory) {
    this.conn = conn;
    this.modelFactory = modelFactory;
  }
  
  
  public void fetchElementRows (String parentTableName, INodePlan elementPlan, int dimension, int parentId, IRepeatingModel repeatingModel) {
    String tableName = buildTableName(parentTableName, elementPlan, dimension);
    String fetchElementSql = SQLBuilder.getFetchElementSql(tableName, elementPlan);
    
    IPreparedStatement stmt = conn.prepareStatement(fetchElementSql);
    stmt.setInt(parentId);
    IResultSet rs = stmt.executeQuery();
    while (rs.next()) {
      int[] parentId2 = new int[1];
      getNodeValue(rs, parentTableName, repeatingModel, parentId2);
    }
    rs.close();
  }

  
  private void getNodeValue (IResultSet rs, String parentTableName, INodeModel nodeModel, int[] parentId) {
    INodePlan nodePlan = nodeModel.getPlan();
    switch (nodePlan.getStructure()) {
    case ARRAY :
    case LIST :
      IRepeatingModel repeatingModel = (IRepeatingModel)nodeModel;
      IRepeatingPlan repeatingPlan = repeatingModel.getPlan();
      fetchElementRows(parentTableName, repeatingPlan.getElementPlan(), repeatingPlan.getDimension(), parentId[0], repeatingModel);
      break;
    case EMBEDDED :
      INameMappedModel embeddedModel = (INameMappedModel)nodeModel;
      getNodeValues (rs, parentTableName, embeddedModel.getMembers(), parentId);
      break;
    case ENTITY :
      throw new IllegalArgumentException("Entity model cannot be the child of any other model");
    case ITEM :
      IItemModel itemModel = (IItemModel)nodeModel;
      if (itemModel.isId()) {
        IItemPlan<Integer> idPlan = itemModel.getPlan();
        parentId[0] = idPlan.getResultValue(rs);
        itemModel.setValue(parentId[0]);
      } else {
        IItemPlan<Object> itemPlan = itemModel.getPlan();
        Object value = itemPlan.getResultValue(rs);
        itemModel.setValue(value);
      }
      break;
    case REFERENCE :
      IReferenceModel referenceModel = (IReferenceModel)nodeModel;
      IReferencePlan<Integer> referencePlan = referenceModel.getPlan();
      Integer referencedId = referencePlan.getResultValue(rs);
      referenceModel.setValue(referencedId);
      break;
    default :
      throw new NotYetImplementedException();
    }
  }
  
  
  private void getNodeValues (IResultSet rs, String parentTableName, List<INodeModel> nodeModels, int[] parentId) {
    for (INodeModel nodeModel : nodeModels) {
      getNodeValue(rs, parentTableName, nodeModel, parentId);
    }
  }

  
  private void getNodeValues (IResultSet rs, String parentTableName, INodeModel[] nodeModels, int[] parentId) {
    for (INodeModel nodeModel : nodeModels) {
      getNodeValue(rs, parentTableName, nodeModel, parentId);
    }
  }

  
  private void getEntityValues (IResultSet rs, String parentTableName, IEntityModel entityModel, int[] parentId) {
    getNodeValues (rs, parentTableName, entityModel.getDataModels(), parentId);
  }

  
  public IEntityModel fetchRowById (String schema, int id, IEntityPlan<?> entityPlan) throws RowNotFoundException {
    String fetchRowSql = SQLBuilder.getFetchByIdSql(schema, entityPlan);
    System.out.println(fetchRowSql);
    
    try (
      IPreparedStatement stmt = conn.prepareStatement(fetchRowSql);
      IResultSet rs = stmt.executeQuery(id))
    {
      if (rs.next()) {
        IEntityModel entityModel = modelFactory.buildEntityModel(entityPlan);
        int[] parentId = new int[1];
        getEntityValues(rs, entityPlan.getName(), entityModel, parentId);
        return entityModel;
      } else {
        throw new RowNotFoundException(entityPlan.getName(), id);
      }
    }
  }

}
