package org.gyfor.object.model;

import org.gyfor.object.plan.IItemPlan;


public class ItemModel extends NodeModel {

  private final IItemPlan<?> itemPlan;
  private Object value;
  
  
  public ItemModel (RootModel rootModel, NodeModel parent, int id, IItemPlan<?> itemPlan) {
    super (rootModel, parent, id);
    this.itemPlan = itemPlan;
  }

  
  @Override
  public void setValue (Object value) {
    this.value = value;
  }
  
  
  @Override
  public Object getValue () {
    return value;
  }
  
  
  @Override
  public IItemPlan<?> getPlan() {
    return itemPlan;
  }
  
  
  public String getName() {
    return itemPlan.getName();
  }
  
  
  @Override
  public String toString () {
    return "ItemModel(" + getId() + "," + itemPlan.getName() + ")";
  }


  @Override
  public boolean isItem () {
    return true;
  }


//  @Override
//  public String toHTML() {
//    int id = getId();
//    String html = "<label id='node-" + id + "' for='" + id + "'>"
//         + "<span>" + plan.getDeclaredLabel() + "</span>"
//         + "<input type='text' name = '" + id + "' id='" + id + "' size='10'>"
//         + "</label>";
//    return html;
//  }

}
