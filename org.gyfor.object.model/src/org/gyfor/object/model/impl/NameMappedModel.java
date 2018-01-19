package org.gyfor.object.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.model.ref.ClassValueReference;
import org.gyfor.object.model.ref.IValueReference;
import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.INodePlan;


public abstract class NameMappedModel extends ContainerModel implements INameMappedModel {

  private final IClassPlan<?> classPlan;
  
  private Map<String, INodeModel> members = new LinkedHashMap<>();
  
  
  public NameMappedModel (ModelFactory modelFactory, IValueReference valueRef, IClassPlan<?> classPlan) {
    super (modelFactory, valueRef, classPlan);
    this.classPlan = classPlan;
  }
  
  
  @Override
  public void syncValue (Object nameMappedValue) {
    if (nameMappedValue == null) {
      INodePlan[] memberPlans = classPlan.getMembers();
      for (INodePlan memberPlan : memberPlans) {
        String fieldName = memberPlan.getName();
        INodeModel member = members.remove(fieldName);
        if (member != null) {
          fireChildRemoved(this, member);
        }
      }
    } else {
      INodePlan[] memberPlans = classPlan.getMembers();
      for (INodePlan memberPlan : memberPlans) {
        System.out.println("ccccccccccc " + classPlan.getName() + " > " + memberPlan.getName());
        String fieldName = memberPlan.getName();
        INodeModel member = members.get(fieldName);
        if (member == null) {
          IValueReference memberValueRef = new ClassValueReference(valueRef, memberPlan);
          member = buildNodeModel(this, memberValueRef, memberPlan);
          members.put(fieldName, member);
          fireChildAdded(this, member);
        }
        System.out.println("bbbbbbbbbbbbbbbbbb 1 " + memberPlan.getName());
        if (memberPlan.isViewOnly() == false) {
          System.out.println("bbbbbbbbbbbbbbbbbb 2");
          Object memberValue = memberPlan.getFieldValue(nameMappedValue);
          System.out.println("bbbbbbbbbbbbbbbbbb 2a" + memberValue);
          member.syncValue(memberValue);
        }
        System.out.println("bbbbbbbbbbbbbbbbbb 3");
      }
    }
  }

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("NameMappedModel {");
    for (Map.Entry<String, INodeModel> member : members.entrySet()) {
      indent(level);
      //System.out.println(member.getKey() + ": ");
      System.out.println(member.getValue().getValueRefName() + ": ");
      member.getValue().dump(level + 1);
    }
    indent(level);
    System.out.println("}");
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodeModel> X getMember(String name) {
    return (X)members.get(name);
  }

  
  @Override
  public INodeModel[] getMembers () {
    INodeModel[] result = new INodeModel[members.size()];
    int i = 0;
    for (INodeModel member : members.values()) {
      result[i++] = member;
    }
    return result;
  }


  @Override
  public Collection<INodeModel> getContainerNodes() {
    //return members.values();
    List<INodeModel> nodes = new ArrayList<>();
    for (Map.Entry<String, INodeModel> entry : members.entrySet()) {
      nodes.add(entry.getValue());
    }
    return nodes;
  }
  

  @Override
  public INodeModel getNameMappedNode(String name) {
    return members.get(name);
  }
  

  @Override
  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after) {
    before.accept(this);
    for (INodeModel member : members.values()) {
      member.walkModel(before, after);
    }
    after.accept(this);
  }
  
    
}
