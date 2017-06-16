package org.gyfor.dao.sql;

import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;

public class NameMappedDAO {

  protected void parse(INameMappedModel model) {
    for (INodeModel node : model.getMembers()) {
      NodeDAO.parseNode(node);
    }
  }
  
}
