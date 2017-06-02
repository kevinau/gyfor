package org.gyfor.object.plan;

import java.lang.reflect.Field;

import org.gyfor.object.EntryMode;

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
public interface INodePlan {

  public default void dump () {
    dump (0);
  }

  public void dump (int level);

  public EntryMode getEntryMode();

  public String getName();
  
  public String getQualifiedName();
  
  public <X> X getFieldValue (Object instance);
  
  public void setFieldValue (Object instance, Object value);
  
  public default void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }

  public <X> X newInstance(X fromValue);

  public INodePlan[] getChildNodes();
  
  public <X extends ILabelGroup> X getLabels();
  
  public boolean isNullable();
  
  public boolean isItem();
  
  public PlanStructure getStructure();

  public Field getField();

  public INodePlan getParent();
  
  public void setParent (INodePlan parent);

}
