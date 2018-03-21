package org.plcore.userio.model;

import java.util.concurrent.atomic.AtomicInteger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.todo.NotYetImplementedException;
import org.plcore.userio.model.impl.ArrayModel;
import org.plcore.userio.model.impl.EmbeddedModel;
import org.plcore.userio.model.impl.EntityModel;
import org.plcore.userio.model.impl.ItemModel;
import org.plcore.userio.model.impl.ListModel;
import org.plcore.userio.model.impl.ReferenceModel;
import org.plcore.userio.model.ref.EntityValueReference;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.plan.IArrayPlan;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.IListPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IReferencePlan;
import org.plcore.userio.plan.PlanFactory;


@Component
public class ModelFactory implements IModelFactory {

  @Reference
  private PlanFactory planFactory;
  
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
  public IEntityModel buildEntityModel(String entityClassName) throws ClassNotFoundException {
    if (planFactory == null) {
      throw new IllegalStateException("No PlanFactory supplied to this model factory");
    }
    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(entityClassName);
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
