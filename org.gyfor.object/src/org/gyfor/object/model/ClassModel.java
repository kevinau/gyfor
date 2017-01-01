package org.gyfor.object.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.object.model.ref.ClassValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;

public abstract class ClassModel extends ContainerModel {

  private final IValueReference valueRef;
  private final IClassPlan classPlan;
  
  private Map<String, NodeModel> memberModels = new LinkedHashMap<>();


  protected ClassModel(RootModel rootModel, ContainerModel parent, int id, IValueReference valueRef, IClassPlan<?> classPlan) {
    super(rootModel, parent, id);
    this.valueRef = valueRef;
    this.classPlan = classPlan;
  }
  
  
  @Override
  public void setValue (Object instance) {
    valueRef.setValue(instance);
    
    for (INodePlan memberPlan : classPlan.getMemberPlans()) {
      Object memberValue = memberPlan.getValue(instance);
      Field field = memberPlan.getField();
      NodeModel memberModel = getRoot().buildNodeModel(getParent(), new ClassValueReference(instance, field), memberPlan);
    }
  }


  public void setValue (String name, Object instance) {
    INodePlan memberPlan = classPlan.getMemberPlan(name);
    if (memberPlan == null) {
      throw new IllegalArgumentException("'" + name + "' is not a known member of " + this);
    }
    setValue (name, memberPlan, instance);
  }


  public void setValue (String memberName, INodePlan memberPlan, Object instance) {
    Object memberValue = memberPlan.getValue(instance);
      
    if (memberPlan instanceof IItemPlan) {
      // Create or update member model
      NodeModel memberModel = getOrCreateMember(memberName, memberPlan);
      memberModel.setValue(memberValue);
    } else {
      if (memberValue == null && memberPlan.isNullable()) {
        // Remove the member model if present
        removeChildModel(memberName);
      } else {
        // Create or update member model
        NodeModel memberModel = getOrCreateMember(memberName, memberPlan);
        memberModel.setValue(memberValue);
      }          
    }
  }


  protected NodeModel getOrCreateMember (String name, INodePlan plan) {
    NodeModel memberModel = memberModels.get(name);
    if (memberModel == null) {
      memberModel = getRoot().buildNodeModel(this, new ClassValueReference(plan);
      memberModels.put(name, memberModel);
      getRoot().fireChildAdded(this, memberModel);
    }
    return memberModel;
  }

  
  @Override
  public Object getValue () {
    return valueRef.getValue();
  }
  
  
  protected void removeChildModel (String name) {
    NodeModel removed = memberModels.remove(name);
    if (removed != null) {
      getRoot().fireChildRemoved(this, removed);
    }
  }


  @SuppressWarnings("unchecked")
  public <X extends NodeModel> X getMember (String name) {
    return (X)memberModels.get(name);
  }
  
  
  @Override
  public List<NodeModel> getMembers () {
    List<NodeModel> memberList = new ArrayList<>(memberModels.size());
    for (Map.Entry<String, NodeModel> entry : memberModels.entrySet()) {
      memberList.add(entry.getValue());
    }
    return memberList;
  }
  
  
  @Override
  public INodePlan getPlan () {
    return classPlan;
  }
  
  
  @Override
  public String toString () {
    return "NameMappedModel(" + getId() + "," + classPlan.getName() + ")";
  }


  @Override
  public boolean isClassModel() {
    return true;
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
