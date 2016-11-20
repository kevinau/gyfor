package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;

/** 
 * A group of labels that can be attached to a reference data entry field.  
 * <p>
 * Currently, this is implemented as a ItemLabelGroup, but this could change in the future.
 */
public class ReferenceLabelGroup extends ItemLabelGroup {

  public ReferenceLabelGroup(Field field, String fieldName) {
    super(field, fieldName);
  }

}
