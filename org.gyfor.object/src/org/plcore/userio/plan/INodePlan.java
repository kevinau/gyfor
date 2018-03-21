package org.plcore.userio.plan;

import java.lang.annotation.Annotation;

import org.plcore.userio.EntryMode;
import org.plcore.userio.INode;

/**
 * The detail of a class field. The plan contains sufficient detail about a
 * class so it can be used as the basis of a data entry form or a persistent
 * entity.
 * <p>
 * The detail here is determined solely from the class field or annotations on
 * the field. It contains no field values or other runtime information. Object
 * plans can be cached.
 * 
 * @author Kevin Holloway
 * 
 */
public interface INodePlan extends INode {

  public default void dump () {
    dump (0);
  }

  public void dump (int level);

  public EntryMode getEntryMode();

  @Override
  public String getName();
  
  public <X> X getFieldValue (Object instance);
  
  public void setFieldValue (Object instance, Object value);
  
  public default void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }

  public <X> X newInstance(X fromValue);

  public <X extends ILabelGroup> X getLabels();
  
  public boolean isNullable();
  
  public boolean isItem();
  
  public PlanStructure getStructure();

  public <A extends Annotation> A getAnnotation(Class<A> klass);

  public boolean isViewOnly();

//  public Field getField();

}
