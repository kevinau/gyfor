package org.gyfor.object.model.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ref.ClassValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INameMappedPlan;
import org.gyfor.object.plan.INodePlan;

public abstract class NameMappedModel extends ContainerModel implements INameMappedModel {

  private final AtomicInteger idSource;
  private final IValueReference valueRef;
  private final INameMappedPlan mappedPlan;
  
  private Map<String, INodeModel> memberModels = new LinkedHashMap<>();

  private Object modelValue = null;
  
  
  protected NameMappedModel(AtomicInteger idSource, IEntityModel entityModel, IContainerModel parent, IValueReference valueRef, INameMappedPlan mappedPlan) {
    super(idSource, entityModel, parent);
    this.idSource = idSource;
    this.valueRef = valueRef;
    this.mappedPlan = mappedPlan;
  }
  
  
  @Override
  public List<INodeModel> getChildNodes () {
    // TODO this could be optimized
    List<INodeModel> children = new ArrayList<>();
    for (INodeModel member : memberModels.values()) {
      children.add(member);
    }
    return children;
  }
  
  
  @Override
  public void setValue (Object value) {
    if (modelValue == null ? value == null : modelValue.equals(value)) {
      // No change in value
    } else {
      valueRef.setValue(value);
      modelValue = value;
    
      if (value == null) {
        for (INodePlan memberPlan : mappedPlan.getMemberPlans()) {
          INodeModel memberModel = memberModels.get(memberPlan.getName());
          if (memberModel != null) {
            memberModels.remove(memberPlan.getName(), memberModel);
            fireChildRemoved(this, memberModel);
          }
        }
      } else {
        for (INodePlan memberPlan : mappedPlan.getMemberPlans()) {
          INodeModel memberModel = memberModels.get(memberPlan.getName());
          if (memberModel == null) {
            Field field = memberPlan.getField();
            memberModel = ModelFactory.buildNodeModel(idSource, getEntity(), this, new ClassValueReference(value, field), memberPlan);
            memberModels.put(memberPlan.getName(), memberModel);
            fireChildAdded(this, memberModel);
          }
  
          Object memberValue = memberPlan.getValue(value);
          memberModel.setValue(memberValue);
        }
      }
    }
  }


  public void setValue (String name, Object instance) {
    INodePlan memberPlan = mappedPlan.getMemberPlan(name);
    if (memberPlan == null) {
      throw new IllegalArgumentException("'" + name + "' is not a known member of " + this);
    }
    setValue (name, memberPlan, instance);
  }


  public void setValue (String memberName, INodePlan memberPlan, Object instance) {
    Object memberValue = memberPlan.getValue(instance);
      
    if (memberPlan instanceof IItemPlan) {
      // Create or update member model
      INodeModel memberModel = getOrCreateMember(memberName, memberPlan);
      memberModel.setValue(memberValue);
    } else {
      if (memberValue == null && memberPlan.isNullable()) {
        // Remove the member model if present
        removeChildModel(memberName);
      } else {
        // Create or update member model
        INodeModel memberModel = getOrCreateMember(memberName, memberPlan);
        memberModel.setValue(memberValue);
      }          
    }
  }


  protected INodeModel getOrCreateMember (String name, INodePlan plan) {
    INodeModel memberModel = memberModels.get(name);
    if (memberModel == null) {
      Field field = plan.getField();
      memberModel = ModelFactory.buildNodeModel(idSource, getEntity(), getParent(), new ClassValueReference(valueRef.getValue(), field), plan);
      memberModels.put(name, memberModel);
      fireChildAdded(this, memberModel);
    }
    return memberModel;
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public <X> X getValue () {
    return (X)modelValue;
  }
  
  
  @Override
  @Deprecated
  public IValueReference getValueRef () {
    return valueRef;
  }
  
  
  protected void removeChildModel (String name) {
    INodeModel removed = memberModels.remove(name);
    if (removed != null) {
      fireChildRemoved(this, removed);
    }
  }


  @Override
  @SuppressWarnings("unchecked")
  public <X extends INodeModel> X getMember (String name) {
    return (X)memberModels.get(name);
  }
  
  
  @Override
  public List<INodeModel> getMembers () {
    List<INodeModel> memberList = new ArrayList<>(memberModels.size());
    for (Map.Entry<String, INodeModel> entry : memberModels.entrySet()) {
      memberList.add(entry.getValue());
    }
    return memberList;
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodePlan> X getPlan () {
    return (X)mappedPlan;
  }
  
  
  @Override
  public String toString () {
    return "NameMappedModel(" + mappedPlan.getName() + ")";
  }


//  @Override
//  public String toHTML() {
//    int id = getId();
//    String html = "<div id='node-" + id + "'>"
//                + "<span id='node-" + id + "'></span>"
//                + "</div>";
//    return html;
//  }

}
