package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ref.ClassValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.INameMappedPlan;
import org.gyfor.object.plan.INodePlan;

public class NameMappedModel extends ContainerModel implements INameMappedModel {

  private final IModelFactory modelFactory;
  private final INameMappedPlan mappedPlan;
  private IValueReference valueRef;

  private Map<String, INodeModel> memberModels = new LinkedHashMap<>();
  
  protected NameMappedModel(IModelFactory modelFactory, IEntityModel entityModel, IContainerModel parent, INameMappedPlan mappedPlan) {
    super(modelFactory, entityModel, parent);
    this.modelFactory = modelFactory;
    this.mappedPlan = mappedPlan;
  }
  
  
  protected void setValueReference (IValueReference valueRef) {
    this.valueRef = valueRef;
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
    Object currentValue = valueRef.getValue();
//    if (currentValue == null ? value == null : currentValue.equals(value)) {
//      // No change in value
//      return;
//    }
    System.out.println("aaaa " + value);
    valueRef.setValue(value);
   
    if (value == null) {
      for (INodePlan memberPlan : mappedPlan.getMemberPlans()) {
        INodeModel memberModel = memberModels.get(memberPlan.getName());
        if (memberModel != null) {
          memberModels.remove(memberPlan.getName(), memberModel);
          fireChildRemoved(this, memberModel);
        }
      }
    } else {
      System.out.println("bbbb " + mappedPlan.getMemberPlans().length);
      for (INodePlan memberPlan : mappedPlan.getMemberPlans()) {
        System.out.println("cccc " + memberPlan.getName());
        INodeModel memberModel = memberModels.get(memberPlan.getName());
        if (memberModel == null) {
          IValueReference valueRef = new ClassValueReference(memberPlan.getField()) {
            @Override
            public Object getInstance() {
              return NameMappedModel.this.valueRef.getValue();
            }
          };
          memberModel = modelFactory.buildNodeModel(getEntity(), this, valueRef, memberPlan);
          System.out.println("dddd " + memberModel);
          System.out.println("eeee " + memberModel.getValue());
          memberModel.dump(0);
          memberModels.put(memberPlan.getName(), memberModel);
          fireChildAdded(this, memberModel, null);
        }
      }
    }
  }


  public void setValue (Object value, Map<String, Map<String, Object>> selection) {
    if (selection == null) {
      setValue(value);
    }

    Object oldValue = getParent().getValue();
    if (oldValue == null ? value == null : oldValue.equals(value)) {
      // No change in value
      return;
    }
    
    getParent().setValue(value);
  
    if (value == null) {
      for (String memberName : selection.keySet()) {
        INodeModel memberModel = memberModels.get(memberName);
        if (memberModel != null) {
          memberModels.remove(memberName, memberModel);
          fireChildRemoved(this, memberModel);
        }
      }
    } else {
      for (String memberName : selection.keySet()) {
        INodeModel memberModel = memberModels.get(memberName);
        if (memberModel == null) {
          INodePlan memberPlan = mappedPlan.getMemberPlan(memberName);
          IValueReference valueRef = new ClassValueReference(memberPlan.getField()) {
            @Override
            public Object getInstance() {
              return NameMappedModel.this.valueRef.getValue();
            }
          };
          memberModel = modelFactory.buildNodeModel(getEntity(), this, valueRef, memberPlan);
          memberModels.put(memberName, memberModel);
          Map<String, Object> context = selection.get(memberName);
          fireChildAdded(this, memberModel, context);
        }
      }
    }
  }


//  public void setValue (String name, Object instance) {
//    INodePlan memberPlan = mappedPlan.getMemberPlan(name);
//    if (memberPlan == null) {
//      throw new IllegalArgumentException("'" + name + "' is not a known member of " + this);
//    }
//    setValue (name, memberPlan, instance);
//  }
//
//
//  public void setValue (String memberName, INodePlan memberPlan, Object instance) {
//    Object memberValue = memberPlan.getValue(instance);
//      
//    if (memberPlan instanceof IItemPlan) {
//      // Create or update member model
//      INodeModel memberModel = getOrCreateMember(memberName, memberPlan);
//      memberModel.setValue(memberValue);
//    } else {
//      if (memberValue == null && memberPlan.isNullable()) {
//        // Remove the member model if present
//        removeChildModel(memberName);
//      } else {
//        // Create or update member model
//        INodeModel memberModel = getOrCreateMember(memberName, memberPlan);
//        memberModel.setValue(memberValue);
//      }          
//    }
//  }
//
//
//  protected INodeModel getOrCreateMember (String name, INodePlan plan) {
//    INodeModel memberModel = memberModels.get(name);
//    if (memberModel == null) {
//      Field field = plan.getField();
//      memberModel = ModelFactory.buildNodeModel(idSource, getEntity(), getParent(), new ClassValueReference(valueRef.getValue(), field), plan);
//      memberModels.put(name, memberModel);
//      fireChildAdded(this, memberModel);
//    }
//    return memberModel;
//  }

  
  @SuppressWarnings("unchecked")
  @Override
  public <X> X getValue () {
    return (X)valueRef.getValue();
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
  public String getName () {
    return mappedPlan.getName();
  }
  
  
  @Override
  public String toString () {
    return "NameMappedModel(" + mappedPlan.getName() + ")";
  }

  
  @Override
  public void dump(int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
    System.out.println("MemoryMappedModel:");
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
