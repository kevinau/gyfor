package org.gyfor.berkeleydb;

import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.context.PlanFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.type.IType;
import org.gyfor.object.type.builtin.StringType;

public class ObjectResultDatabaseEntry extends ResultDatabaseEntry {

  private static final long serialVersionUID = 1L;
  
  private final IEntityPlan<?> entityPlan;
  
  
  public ObjectResultDatabaseEntry (PlanFactory envmt, Class<?> klass) {
    entityPlan = EntityPlanFactory.getEntityPlan(envmt, klass);
  }
  
  
  public Object getEntry () {
    Object instance = entityPlan.newInstance();
    getEntry (instance);
    return instance;
  }
  
  
  private void getEntry (Object instance) {
    INodePlan[] nodePlans = entityPlan.getMemberPlans();
    for (INodePlan nodePlan : nodePlans) {
      switch (nodePlan.getStructure()) {
      case ARRAY :
        break;
      case EMBEDDED :
        break;
      case ENTITY :
        throw new RuntimeException("Entity cannot contain entity members");
      case INTERFACE :
        break;
      case ITEM :
        IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
        Object v = getItemValue(itemPlan.getType(), itemPlan.isNullable());
        itemPlan.setValue(instance, v);
        break;
      case LIST :
        break;
      case MAP :
        break;
      case REFERENCE :
        break;
      case SET :
        break;
      }
      Object value = super.getString();
      nodePlan.setValue(instance, value);
    }
  }
  
  
  private Object getItemValue (IType<?> type, boolean isNullable) {
    super.getBoolean()
    if (type instanceof StringType) {
      return getString();
    } else if (type )
  }
}
