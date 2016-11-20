package org.gyfor.object.model;

import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;


/*
 * This is the topmost model in the herirachy.
 */
public class RootModel {

  private AtomicInteger idSource = new AtomicInteger(1);
  
  private FastAccessList<ModelChangeListener> modelChangeListenerList = new FastAccessList<>(ModelChangeListener.class);

  
  public RootModel () {
  }
  
  
  public void addStructureChangeListener(ModelChangeListener x) {
    modelChangeListenerList.add(x);
  }
  
  protected void fireChildAdded(NodeModel parent, NodeModel node) {
    for (ModelChangeListener listener : modelChangeListenerList) {
      listener.childAdded(parent, node);
    }
  }


  protected void fireChildRemoved(NodeModel parent, NodeModel node) {
    for (ModelChangeListener listener : modelChangeListenerList) {
      listener.childRemoved(parent, node);
    }
  }

  
  public int nextId() {
    return idSource.getAndIncrement();
  }
  
  
  protected NodeModel buildNodeModel (NodeModel parent, INodePlan plan) {
    return buildNodeModel (parent, nextId(), plan);
  }
  
  
  protected NodeModel buildNodeModel (NodeModel parent, int id, INodePlan plan) {
    if (plan instanceof IItemPlan) {
      return new ItemModel(this, parent, id, (IItemPlan<?>)plan);
    } else if (plan instanceof IEntityPlan) {
      return new EntityModel(this, null, id, (IEntityPlan<?>)plan);
    } else {
      throw new RuntimeException("Unsupported plan: " + plan.getClass());
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
  

  public EntityModel buildEntityModel (int id, IEntityPlan<?> entityPlan) {
    EntityModel entityModel = new EntityModel(this, null, id, entityPlan);
    return entityModel;
  }
  

  public EntityModel buildEntityModel (int id, IEntityPlan<?> entityPlan, Object instance) {
    EntityModel entityModel = new EntityModel(this, null, id, entityPlan, instance);
    return entityModel;
  }
  

  public EntityModel buildEntityModel (IEntityPlan<?> entityPlan) {
    return buildEntityModel (nextId(), entityPlan);
  }
  

  public EntityModel buildEntityModel (IEntityPlan<?> entityPlan, Object instance) {
    return buildEntityModel (nextId(), entityPlan, instance);
  }
  

  @Override
  public String toString () {
    return "RootModel()";
  }

}
