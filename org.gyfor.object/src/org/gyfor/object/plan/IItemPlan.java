package org.gyfor.object.plan;

import java.lang.annotation.Annotation;

import org.gyfor.object.type.IType;

public interface IItemPlan<T> extends INodePlan {

  /** 
   * The type of this input field.
   */
  public IType<T> getType();

  /**
   * A convenience method that returns an Annotation for this input field.
   */
  public <A extends Annotation> A getAnnotation(Class<A> klass);

  /**
   * Is an empty input field acceptable.  If this is true, an empty input field is acceptable
   * and the resultant field value is <code>null</code>.  If this is false, an empty input field
   * is reported as an error.
   * <p>
   * Note that, it is possible that the input checking of IType does not allow an empty input field.  If
   * this method returns true, the error checking of IType is bypassed and the field value is 
   * set to <code>null</code>.
   * <p>
   * For primitive Java types, this method always returns false.
   */
  @Override
  public boolean isNullable();

  @Override
  public <X> X getValue(Object instance);

  @Override
  public void setValue(Object instance, Object value);

  public boolean isDescribing();
  
}
