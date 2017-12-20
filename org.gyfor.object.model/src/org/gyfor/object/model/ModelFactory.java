package org.gyfor.object.model;

import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.model.impl.ArrayModel;
import org.gyfor.object.model.impl.EmbeddedModel;
import org.gyfor.object.model.impl.EntityModel;
import org.gyfor.object.model.impl.ItemModel;
import org.gyfor.object.model.impl.ListModel;
import org.gyfor.object.model.impl.ReferenceModel;
import org.gyfor.object.model.ref.EntityValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IArrayPlan;
import org.gyfor.object.plan.IEmbeddedPlan;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.IListPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IReferencePlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.todo.NotYetImplementedException;


public class ModelFactory implements IModelFactory {

  private final PlanFactory planFactory;
  private final AtomicInteger idSource = new AtomicInteger(0);
  
  public ModelFactory (PlanFactory planFactory) {
    this.planFactory = planFactory;
  }
  
  
  public ModelFactory () {
    this.planFactory = null;
  }
  
  
  public int getNodeId() {
    return idSource.incrementAndGet();
  }
  
  
  @Override
  public IEntityModel buildEntityModel(Class<?> entityClass) {
    if (planFactory == null) {
      throw new IllegalStateException("No PlanFactory supplied to this model factory");
    }
    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(entityClass);
    return buildEntityModel(entityPlan);
  }
  
  
  @Override
  public IEntityModel buildEntityModel(IEntityPlan<?> entityPlan) {
    IValueReference valueRef = new EntityValueReference();
    return (IEntityModel)buildNodeModel(valueRef, entityPlan);
  }
  
  
  public INodeModel buildNodeModel(IValueReference valueRef, INodePlan nodePlan) {
    switch (nodePlan.getStructure()) {
    case ARRAY :
      return new ArrayModel(this, valueRef, (IArrayPlan)nodePlan);
    case EMBEDDED :
      return new EmbeddedModel(this, valueRef, (IEmbeddedPlan<?>)nodePlan);  
    case ENTITY :
      return new EntityModel(this, valueRef, (IEntityPlan<?>)nodePlan);  
    case ITEM :
      return new ItemModel(this, valueRef, (IItemPlan<?>)nodePlan);
    case LIST :
      return new ListModel(this, valueRef, (IListPlan)nodePlan);
    case REFERENCE :
      return new ReferenceModel(this, valueRef, (IReferencePlan<?>)nodePlan);  
    default :
      throw new NotYetImplementedException("buildNodeModel from a nodePlan: " + nodePlan.getClass());
    }
  }
}
