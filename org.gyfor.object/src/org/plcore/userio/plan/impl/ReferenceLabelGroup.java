package org.plcore.userio.plan.impl;

import org.plcore.userio.plan.ItemLabelGroup;
import org.plcore.userio.plan.MemberValueGetterSetter;

/** 
 * A group of labels that can be attached to a reference data entry field.  
 * <p>
 * Currently, this is implemented as a ItemLabelGroup, but this could change in the future.
 */
public class ReferenceLabelGroup extends ItemLabelGroup {

  public ReferenceLabelGroup (String label, String hint, String description) {
    super(label, hint, description);
  }

  public ReferenceLabelGroup (MemberValueGetterSetter field, String fieldName) {
    super(field, fieldName);
  }

}
