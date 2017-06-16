package org.gyfor.dao.sql;

import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IRepeatingModel;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.todo.NotYetImplementedException;

public class RepeatingDAO {

  public void parse (IRepeatingModel model) {
    IPreparedStatement stmt = buildAddChildStatement(model.getId(), model.getPlan().getElementPlan());
    for (INodeModel node : model.getMembers()) {
      NodeDAO.parseNode(node);
    }
  }
  
  private IPreparedStatement buildAddChildStatement(int parentId, INodePlan plan) {
    StringBuilder sql = new StringBuilder();
    sql.append("insert into XX values (?");
    switch (plan.getStructure()) {
    case ITEM :
      sql.append(",?");
      break;
    case EMBEDDED :
      buildPlaceholders(sql, plan);
      break;
    case REFERENCE :
      sql.append(",?");
      break;
    case ENTITY :
      throw new IllegalArgumentException("Entity plans are top level plans only");
    case ARRAY :
      break;
    case INTERFACE :
      throw new NotYetImplementedException();
    case LIST :
      break;
    case MAP :
      throw new NotYetImplementedException();
    case SET :
      throw new NotYetImplementedException();
    }
  }
}
