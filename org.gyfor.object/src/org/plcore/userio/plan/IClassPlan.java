package org.plcore.userio.plan;

import java.util.List;
import java.util.Set;


public interface IClassPlan<T> extends IContainerPlan {

  public List<IRuntimeDefaultProvider> getRuntimeDefaultProviders();

  public List<IRuntimeValuesProvider> getRuntimeValuesProviders();

  public List<IRuntimeFactoryProvider> getRuntimeFactoryProviders();

  public List<IRuntimeImplementationProvider> getRuntimeImplementationProviders();

  //public List<IRuntimeLabelProvider> getRuntimeLabelProviders();

  public List<IRuntimeModeProvider> getRuntimeModeProviders();

  public List<IRuntimeOccursProvider> getRuntimeOccursProviders();

  //public List<IRuntimeTypeProvider> getRuntimeTypeProviders();

  public Set<IValidationMethod> getValidationMethods();

  public Class<?> getSourceClass();

  @Override
  public default void dump () {
    dump (0);
  }

  @Override
  public void dump (int level);

  public String getClassName();

  public INodePlan[] getMembers();

  public <X extends INodePlan> X getMember(String name);

  public MemberValueGetterSetter getNodeField(String memberName);

}
