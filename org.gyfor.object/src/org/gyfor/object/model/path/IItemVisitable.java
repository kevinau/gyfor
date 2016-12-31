package org.gyfor.object.model.path;

import org.gyfor.object.model.ItemModel;

public interface IItemVisitable {

  /**
   * Visit the item node.
   */
  public void visit(ItemModel model);

}
