package org.gyfor.object.plan;

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
  
  public <X> X getValue (Object instance);
  
  public void setValue (Object instance, Object value);
  
  public default void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }

  public ILabelGroup getLabels();
  
  public boolean isNullable();
  
  public boolean isItem();
  
}
