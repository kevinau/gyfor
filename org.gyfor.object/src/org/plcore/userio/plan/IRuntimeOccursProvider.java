package org.plcore.userio.plan;

import org.plcore.userio.path.IPathExpression;

public interface IRuntimeOccursProvider extends IRuntimeProvider {

  /**
   * Get a list of XPaths expressions that identify the array field that this plan
   * applies to. All matching fields will use the same getType method.
   * The list should never be empty, but there is no problem if it is. The
   * XPaths here are relative to the control which contains the
   * ISizeProviderMethod.
   * 
   * @return list of XPath expressions
   */
  @Override
  public IPathExpression[] getAppliesTo();
  

  /**
   * Get a list of field names that the getFieldUse method depends on. Some
   * implementations may compute this from the code of the getFieldUse method,
   * others will specify it explicitly.  The names here are relative to the control
   * which contains the runtime mode provider.
   * 
   * @return list of field names
   */
  @Override
  public IPathExpression[] getDependsOn();

  
  /**
   * Get the size for the designated array field. The designated array fields are
   * those listed by the getAppliesTo method.
   * 
   * @return the size of the designated array field.
   */
  public int getOccurs(Object instance);

  /**
   * Does this provider return an initial size, or does it only return a 
   * size during runtime.  A provider that returns a size at runtime requires
   * an instance value.
   * 
   * @return
   */
  @Override
  public boolean isRuntime ();

}
