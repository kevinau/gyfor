package org.gyfor.object.model.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.EffectiveEntryModeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ItemEventListener;
import org.gyfor.object.model.ref.EntityValueReference;
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
  private final FastAccessList<EffectiveEntryModeListener> effectiveModeListenerList = new FastAccessList<>(EffectiveEntryModeListener.class);
  private final FastAccessList<ItemEventListener> itemEventListenerList = new FastAccessList<>(ItemEventListener.class);

  
  public RootModel () {
  }
  
  
  public void addContainerChangeListener(ContainerChangeListener x) {
    containerChangeListenerList.add(x);
  }
  
  
  public void removeContainerChangeListener(ContainerChangeListener x) {
    containerChangeListenerList.remove(x);
  }
  
  
  public void addEffectiveModeListener(EffectiveEntryModeListener x) {
    effectiveModeListenerList.add(x);
  }
  
  
  public void removeEffectiveModeListener(EffectiveEntryModeListener x) {
    effectiveModeListenerList.remove(x);
  }
  
  
  public void addItemEventListener(ItemEventListener x) {
    itemEventListenerList.add(x);
  }
  
  
  public void removeItemEventListener(ItemEventListener x) {
    itemEventListenerList.remove(x);
  }
  
  
  void fireChildAdded(ContainerModel parent, INodeModel node, Map<String, Object> context) {
    for (ContainerChangeListener listener : containerChangeListenerList) {
      listener.childAdded(parent, node, context);
    }
  }


  void fireChildRemoved(ContainerModel parent, INodeModel node) {
    for (ContainerChangeListener listener : containerChangeListenerList) {
      listener.childRemoved(parent, node);
    }
  }

  
  void fireValueEqualityChange(ItemModel model) {
    for (ItemEventListener listener : itemEventListenerList) {
      listener.valueEqualityChange(model);
    }
  }


  void fireSourceEqualityChange(ItemModel model) {
    for (ItemEventListener listener : itemEventListenerList) {
      listener.sourceEqualityChange(model);
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


  void fireEffectiveModeChanged(INodeModel model) {
    for (EffectiveEntryModeListener listener : effectiveModeListenerList) {
      listener.effectiveModeChanged(model);
    }
  }


  public int nextId() {
    return idSource.getAndIncrement();
  }
  
  
  protected NodeModel buildNodeModel (IEntityModel entityModel, IContainerModel parent, IValueReference valueRef, INodePlan plan) {
    return buildNodeModel (idSource, entityModel, parent, valueRef, plan);
  }
  
  
  protected NodeModel buildNodeModel (AtomicInteger idSource, IEntityModel entityModel, IContainerModel parent, IValueReference valueRef, INodePlan plan) {
    if (plan instanceof IItemPlan) {
      return new ItemModel(idSource, entityModel, parent, valueRef, (IItemPlan<?>)plan);
    } else if (plan instanceof IEntityPlan) {
      return new EntityModel(idSource, entityModel, parent, valueRef, (IEntityPlan<?>)plan);
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
  

  public EntityModel buildEntityModel (IEntityPlan<?> entityPlan) {
    EntityValueReference valueRef = new EntityValueReference();
    EntityModel entityModel = new EntityModel(idSource, entityPlan);
    return entityModel;
  }
  

  public EntityModel buildEntityModel (IEntityPlan<?> entityPlan, Object instance) {
    EntityValueReference valueRef = new EntityValueReference();
    EntityModel entityModel = new EntityModel(idSource, entityPlan, instance);
    return entityModel;
  }
  

  @Override
  public String toString () {
    return "RootModel()";
  }


}
