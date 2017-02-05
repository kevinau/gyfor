package org.gyfor.object.model.impl;

import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.todo.NotYetImplementedException;



public class ModelFactory {

  protected static INodeModel buildNodeModel (AtomicInteger idSource, IEntityModel entityModel, IContainerModel parent, IValueReference valueRef, INodePlan plan) {
    if (plan instanceof IItemPlan) {
      return new ItemModel(idSource, entityModel, parent, valueRef, (IItemPlan<?>)plan);
    } else if (plan instanceof IEntityPlan) {
      throw new IllegalArgumentException("An entity plan cannot be the child of any plan");
    } else {
      throw new NotYetImplementedException("Plan: " + plan.getClass());
    }
  }
  
//  @SuppressWarnings("unchecked")
//  private NodeModel buildNodeModel (NodeModel parent, INodePlan plan, Object instance) {
//    switch (plan.kind()) {
//    case EMBEDDED:
//      return buildEmbeddedModel(parent, (IEmbeddedPlan<?>)plan, instance);
//    case ENTITY:
//      return buildEntityModel(parent, (IEntityPlan<Object>)plan, instance);
//    case INTERFACE:
//      throw new RuntimeException("Not yet implementd");
//    case ITEM:
//      return buildItemModel(parent, (IItemPlan<?>)plan, instance);
//    case MAP:
//      throw new RuntimeException("Not yet implementd");
//    case REFERENCE:
//      return buildReferenceModel(parent, (IReferencePlan<?>)plan, instance);
//    case REPEATING:
//      return buildRepeatingModel(parent, (IRepeatingPlan)plan, instance);
//    }
//    throw new IllegalArgumentException("Unknown plan kind: " + plan.kind());
//  }
//  
//  
//  public ItemModel buildItemModel (NodeModel parent, int id, IItemPlan<?> itemPlan) {
//    ItemModel itemModel = new ItemModel(this, parent, id, itemPlan);
//    return itemModel;
//  }
//  
//  
//  public NodeModel buildReferenceModel (NodeModel parent, IReferencePlan<?> referencePlan, Object instance) {
//    NodeModel referenceModel;
//
//    IEntityPlan<?> referencedPlan = referencePlan.getReferencedPlan();
//    List<IItemPlan<?>[]> uniqueConstraints = referencedPlan.getUniqueConstraints();
//    if (uniqueConstraints == null || uniqueConstraints.size() == 0) {
//      IItemPlan<?> idPlan = referencedPlan.getIdPlan();
//      Object idValue = idPlan.getValue(instance);
//      referenceModel = buildNodeModel(parent, idPlan, idValue);
//    } else {
//      IItemPlan<?>[] primaryConstraints = uniqueConstraints.get(0);
//      if (primaryConstraints.length == 1) {  
//        IItemPlan<?> primaryConstraint = primaryConstraints[0];
//        Object primaryValue = primaryConstraint.getValue(instance);
//        referenceModel = buildNodeModel(parent, primaryConstraint, primaryValue);
//      } else {
//        MultiModel multiModel = new MultiModel(this, parent, primaryConstraints);
//        fireChildAdded(parent, multiModel);
//        int i = 0;
//        for (INodePlan primaryConstraint : primaryConstraints) {
//          Object nameValue = primaryConstraint.getValue(instance);
//          NodeModel nameModel = buildNodeModel(multiModel, primaryConstraint, nameValue);
//          multiModel.setChildModel(i, nameModel);
//        }
//        referenceModel = multiModel;
//      }
//    }
//    return referenceModel;
//  }
//  
//
//  public MappedModel buildRepeatingModel (NodeModel parent, IRepeatingPlan repeatingPlan, Object instance) {
//    MappedModel containerModel = new MappedModel(this, parent, repeatingPlan, instance);
//    fireChildAdded(parent, containerModel);
//
//    // Add members
//    INodePlan elementClassPlan = repeatingPlan.getElementPlan();
//    for (Iterator<Object> i = repeatingPlan.getIterator(instance); i.hasNext(); ) {
//      Object elementValue = i.next();
//      NodeModel elementModel = buildNodeModel(containerModel, elementClassPlan, elementValue);
//      containerModel.addChildModel(elementModel);
//    }
//    return containerModel;
//  }
//  
//
//  public MappedModel buildEmbeddedModel (NodeModel parent, IEmbeddedPlan<?> embeddedPlan, Object instance) {
//    MappedModel embeddedModel = new MappedModel(this, parent, embeddedPlan, instance);
//    fireChildAdded(parent, embeddedModel);
//
//    // Add members
//    IClassPlan<?> underlyingClassPlan = embeddedPlan.getClassPlan();
//    for (INodePlan memberPlan : underlyingClassPlan.getNodePlans()) {
//      Object memberValue = memberPlan.getValue(instance);
//      NodeModel memberModel = buildNodeModel(embeddedModel, memberPlan, memberValue);
//      embeddedModel.addChildModel(memberModel);
//    }
//    return embeddedModel;
//  }
  

//  public EntityModel2 buildEntityModel (int id, IEntityPlan<?> entityPlan) {
//    EntityModel2 entityModel = new EntityModel2(this, null, id, entityPlan);
//    return entityModel;
//  }
//  
//
//  public EntityModel2 buildEntityModel (int id, IEntityPlan<?> entityPlan, Object instance) {
//    EntityModel2 entityModel = new EntityModel2(this, null, id, entityPlan, instance);
//    return entityModel;
//  }
//  
//
//  public EntityModel2 buildEntityModel (IEntityPlan<?> entityPlan) {
//    return buildEntityModel (nextId(), entityPlan);
//  }
//  
//
//  public EntityModel2 buildEntityModel (IEntityPlan<?> entityPlan, Object instance) {
//    return buildEntityModel (nextId(), entityPlan, instance);
//  }
  
}
