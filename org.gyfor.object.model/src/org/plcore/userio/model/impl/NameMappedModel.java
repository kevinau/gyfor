package org.plcore.userio.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.plcore.userio.INode;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INameMappedModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ItemEventAdapter;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.ClassValueReference;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.plan.IClassPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IRuntimeDefaultProvider;


public abstract class NameMappedModel extends ContainerModel implements INameMappedModel {

  private final IClassPlan<?> classPlan;
  
  private Map<String, INodeModel> members = new LinkedHashMap<>();
  
  
  public NameMappedModel (ModelFactory modelFactory, IValueReference valueRef, IClassPlan<?> classPlan) {
    super (modelFactory, valueRef, classPlan);
    this.classPlan = classPlan;
    
//    // The model has now been constructed.  Set up the value change event handlers.
//    for (IRuntimeDefaultProvider defaultProvider : classPlan.getRuntimeDefaultProviders()) {
//      if (defaultProvider.isRuntime()) {
//        for (String dependsOn : defaultProvider.getDependsOn()) {
//          for (IItemModel itemModel : selectItemModels(dependsOn)) {
//            itemModel.addItemEventListener(new ItemEventAdapter() {
//              @Override
//              public void valueChange(INodeModel node) {
//                Object value = defaultProvider.getDefaultValue(valueRef.getValue());
//                ((IItemModel)node).setDefaultValue(value);
//              }
//            });
//          }
//        }
//      }
//    }

//    // In addition, run all the static default providers to set up
//    // the defaults.
//    for (IRuntimeDefaultProvider<? extends INode> defaultProvider : classPlan.getRuntimeDefaultProviders()) {
//      if (!defaultProvider.isRuntime()) {
//        Object defaultValue = defaultProvider.getDefaultValue(null);
//        for (IPathExpression<? extends INode> appliesTo : defaultProvider.getAppliesTo()) {
//          for (IItemModel itemModel : selectItemModels((IPathExpression<INodeModel>)appliesTo)) {
//            itemModel.setDefaultValue(defaultValue);
//          }
//        }
//      }
//    }
  }
  
  
  //@SuppressWarnings("unchecked")
  private void setupRuntimeDefaults(IItemModel itemModel) {
    for (IRuntimeDefaultProvider defaultProvider : classPlan.getRuntimeDefaultProviders()) {
      if (defaultProvider.isRuntime()) {
        for (IPathExpression expr : defaultProvider.getDependsOn()) {
          if (itemModel.matches(this, expr)) {
            itemModel.addItemEventListener(new ItemEventAdapter() {
              @Override
              public void valueChange(INodeModel node) {
                Object value = defaultProvider.getDefaultValue(valueRef.getValue());
                for (IPathExpression appliesTo : defaultProvider.getAppliesTo()) {
                  List<IItemModel> appliesToModels = NameMappedModel.this.selectItemModels(appliesTo);
                  for (IItemModel appliesToModel : appliesToModels) {
                    appliesToModel.setDefaultValue(value);                    
                  }
                }
              }
            });
          }
        }
      }
    }

    // In addition, run all the runtime default providers to set up
    // the defaults.  After this setup, the runtime event handlers 
    // will keep them up to date.
    for (IRuntimeDefaultProvider defaultProvider : classPlan.getRuntimeDefaultProviders()) {
      Object defaultValue = null;
      boolean defaultCalculated = false;
      for (IPathExpression expr : defaultProvider.getAppliesTo()) {
        if (itemModel.matches(this, (IPathExpression)expr)) {
          // Are there valid values for all dependencys
          boolean inError = false;
          
          loop:
          for (IPathExpression expr2 : defaultProvider.getDependsOn()) {
            List<IItemModel> dependents = selectItemModels((IPathExpression)expr2);
            for (IItemModel dependent : dependents) {
              if (dependent.isInError()) {
                inError = true;
                break loop;
              }
            }
          }
          if (!inError) {
            if (!defaultCalculated) {
              defaultValue = defaultProvider.getDefaultValue(getValue());
              defaultCalculated = true;
            }
            itemModel.setDefaultValue(defaultValue);
          }
        }
      }
    }
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
        String fieldName = memberPlan.getName();
        INodeModel member = members.get(fieldName);
        if (member == null) {
          IValueReference memberValueRef = new ClassValueReference(valueRef, memberPlan);
          member = buildNodeModel(this, memberValueRef, memberPlan);
          // Apply runtime default providers
          members.put(fieldName, member);
          if (member instanceof IItemModel) {
            // Member value may be overridden by setupRuntimeDefaults
            Object memberValue = memberValueRef.getValue();
            setupRuntimeDefaults((IItemModel)member);
            ((IItemModel)member).setValue(memberValue);
          }
          fireChildAdded(this, member);
        }
        if (memberPlan.isViewOnly() == false) {
          Object memberValue = memberPlan.getFieldValue(nameMappedValue);
          member.syncValue(memberValue);
        }
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
  

  @SuppressWarnings("unchecked")
  @Override
  public INode getNameMappedNode(String name) {
    return (INode)members.get(name);
  }
  

  @Override
  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after) {
    before.accept(this);
    for (INodeModel member : members.values()) {
      member.walkModel(before, after);
    }
    after.accept(this);
  }
  
    
  @Override
  public void walkItems(Consumer<IItemModel> consumer) {
    for (INodeModel member : members.values()) {
      member.walkItems(consumer);
    }
  }
  
    
}
