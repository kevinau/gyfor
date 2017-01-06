package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.gyfor.object.EntryMode;
import org.gyfor.object.IPlanEnvironment;
import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IRuntimeDefaultProvider;
import org.gyfor.object.plan.IRuntimeFactoryProvider;
import org.gyfor.object.plan.IRuntimeImplementationProvider;
import org.gyfor.object.plan.IRuntimeLabelProvider;
import org.gyfor.object.plan.IRuntimeModeProvider;
import org.gyfor.object.plan.IRuntimeOccursProvider;
import org.gyfor.object.plan.IRuntimeTypeProvider;
import org.gyfor.object.plan.IValidationMethod;

public abstract class ClassPlan<T> extends NodePlan implements IClassPlan<T> {

  private final AugmentedClass<T> augmented;
  
  
  public ClassPlan(IPlanEnvironment context, Field field, Class<T> nodeClass, String name, EntryMode entryMode) {
    super(field, name, entryMode);
    augmented = context.getClassPlan(nodeClass);
  }

  
  @Override
  public INodePlan[] getMemberPlans() {
    return augmented.getMemberPlans();
  }

  
  @Override
  public INodePlan getMemberPlan(String name) {
    return augmented.getMemberPlan(name);
  }

  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("NameMappedPlan: " + augmented.getClassName());
    augmented.dump(level + 1);
  }


  @Override
  public Field getNodeField(String memberName) {
    return augmented.getNodeField(memberName);
  }


  @Override
  public List<IRuntimeDefaultProvider> getRuntimeDefaultProviders() {
    return augmented.getRuntimeDefaultProviders();
  }


  @Override
  public List<IRuntimeFactoryProvider> getRuntimeFactoryProviders() {
    return augmented.getRuntimeFactoryProviders();
  }


  @Override
  public List<IRuntimeImplementationProvider> getRuntimeImplementationProviders() {
    return augmented.getRuntimeImplementationProviders();
  }


  @Override
  public List<IRuntimeLabelProvider> getRuntimeLabelProviders() {
    return augmented.getRuntimeLabelProviders();
  }


  @Override
  public List<IRuntimeModeProvider> getRuntimeModeProviders() {
    return augmented.getRuntimeModeProviders();
  }


  @Override
  public List<IRuntimeOccursProvider> getRuntimeOccursProviders() {
    return augmented.getRuntimeOccursProviders();
  }


  @Override
  public List<IRuntimeTypeProvider> getRuntimeTypeProviders() {
    return augmented.getRuntimeTypeProviders();
  }


  @Override
  public Set<IValidationMethod> getValidationMethods() {
    return augmented.getValidationMethods();
  }


  @Override
  public Class<?> getSourceClass() {
    return augmented.getSourceClass();
  }


  @Override
  public String getClassName() {
    return augmented.getClassName();
  }

}
