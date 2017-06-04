package org.gyfor.object.model.impl;

import java.util.HashMap;
import java.util.Map;

import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.IValueReference;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.INodePlan;


public abstract class NameMappedModel extends ContainerModel implements INameMappedModel {

  private final IClassPlan<?> classPlan;
  
  private Map<String, INodeModel> members = new HashMap<>();
  
  
  public NameMappedModel (ModelFactory modelFactory, IValueReference valueRef, IClassPlan<?> classPlan) {
    super (modelFactory, valueRef, classPlan);
    this.classPlan = classPlan;
  }
  
  
  @Override
  public void syncValue (IContainerModel parent, Object nameMappedValue) {
    setParent(parent);
    if (nameMappedValue == null) {
      for (String memberName : members.keySet()) {
        INodeModel member = members.remove(memberName);
        fireChildRemoved(this, member);
      }
    } else {
      INodePlan[] memberPlans = classPlan.getMembers();
      for (INodePlan memberPlan : memberPlans) {
        String fieldName = memberPlan.getName();
        INodeModel member = members.get(fieldName);
        if (member == null) {
          IValueReference memberValueRef = new ClassValueReference(valueRef, memberPlan);
          member = buildNodeModel(memberValueRef, memberPlan);
          members.put(fieldName, member);
          fireChildAdded(this, member, null);
        }
        Object memberValue = memberPlan.getFieldValue(nameMappedValue);
        member.syncValue(this, memberValue);
      }
    }
  }

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("NameMappedModel {");
    for (Map.Entry<String, INodeModel> member : members.entrySet()) {
      indent(level);
      System.out.println(member.getKey() + ": ");
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
  public INodeModel[] getChildNodes() {
    return getMembers();
  }
}
