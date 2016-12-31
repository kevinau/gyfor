package org.gyfor.object.model;

import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;


/*
 * This is the topmost model in the herirachy.
 */
public class RootModel {

  private AtomicInteger idSource = new AtomicInteger(1);
  
  private final FastAccessList<ContainerChangeListener> containerChangeListenerList = new FastAccessList<>(ContainerChangeListener.class);
  private final FastAccessList<EffectiveModeListener> effectiveModeListenerList = new FastAccessList<>(EffectiveModeListener.class);
  private final FastAccessList<ItemEventListener> itemEventListenerList = new FastAccessList<>(ItemEventListener.class);

  
  public RootModel () {
  }
  
  
  public void addContainerChangeListener(ContainerChangeListener x) {
    containerChangeListenerList.add(x);
  }
  
  
  public void removeContainerChangeListener(ContainerChangeListener x) {
    containerChangeListenerList.remove(x);
  }
  
  
  public void addEffectiveModeListener(EffectiveModeListener x) {
    effectiveModeListenerList.add(x);
  }
  
  
  public void removeEffectiveModeListener(EffectiveModeListener x) {
    effectiveModeListenerList.remove(x);
  }
  
  
  public void addItemEventListener(ItemEventListener x) {
    itemEventListenerList.add(x);
  }
  
  
  public void removeItemEventListener(ItemEventListener x) {
    itemEventListenerList.remove(x);
  }
  
  
  void fireChildAdded(ContainerModel parent, NodeModel node) {
    for (ContainerChangeListener listener : containerChangeListenerList) {
      listener.childAdded(parent, node);
    }
  }


  void fireChildRemoved(ContainerModel parent, NodeModel node) {
    for (ContainerChangeListener listener : containerChangeListenerList) {
      listener.childRemoved(parent, node);
    }
  }

  
  void fireCompareEqualityChange(ItemModel model) {
    for (ItemEventListener listener : itemEventListenerList) {
      listener.compareEqualityChange(model);
    }
  }


  void fireCompareSourceChange(ItemModel model, boolean isDataTrigger) {
    for (ItemEventListener listener : itemEventListenerList) {
      listener.compareSourceChange(model, isDataTrigger);
    }
  }


  void fireComparisonBasisChange(ItemModel model) {
    for (ItemEventListener listener : itemEventListenerList) {
      listener.comparisonBasisChange(model);
    }
  }


  void fireErrorNoted(ItemModel model, UserEntryException ex) {
    for (ItemEventListener listener : itemEventListenerList) {
      listener.errorNoted(model, ex);
    }
  }


  void fireErrorCleared(ItemModel model) {
    for (ItemEventListener listener : itemEventListenerList) {
      listener.errorCleared(model);
    }
  }


  void fireErrorCleared(NodeModel model) {
    for (EffectiveModeListener listener : effectiveModeListenerList) {
      listener.modeChange(model);
    }
  }


  public int nextId() {
    return idSource.getAndIncrement();
  }
  
  
  protected NodeModel buildNodeModel (ContainerModel parent, INodePlan plan) {
    return buildNodeModel (parent, nextId(), plan);
  }
  
  
  protected NodeModel buildNodeModel (ContainerModel parent, int id, IValueReference valueRef, INodePlan plan) {
    if (plan instanceof IItemPlan) {
      return new ItemModel(this, parent, id, valueRef, (IItemPlan<?>)plan);
    } else if (plan instanceof IEntityPlan) {
      return new EntityModel(this, null, id, valueRefm (IEntityPlan<?>)plan);
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
