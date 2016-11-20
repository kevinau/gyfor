package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.gyfor.object.Label;
import org.gyfor.object.plan.ILabelGroup;
import org.gyfor.util.CamelCase;

public class ItemLabelGroup implements ILabelGroup {

  private final String label;

  private final String hint;

  private final String description;

  public ItemLabelGroup(String label, String hint, String description) {
    this.label = label;
    this.hint = hint;
    this.description = description;
  }

  public ItemLabelGroup(Field field, String fieldName) {
    if (field == null) {
      label = CamelCase.toSentence(fieldName);
      hint = "";
      description = "";
    } else {
      Label labelAnn = field.getAnnotation(Label.class);
      if (labelAnn == null) {
        label = CamelCase.toSentence(fieldName);
        hint = "";
        description = "";
      } else {
        if (labelAnn.value().equals("\u0000")) {
          label = CamelCase.toSentence(fieldName);
        } else {
          label = labelAnn.value();
        }
        hint = labelAnn.hint();
        description = labelAnn.description();
      }
    }
  }

  public String getLabel() {
    return label;
  }

  public String getHint() {
    return hint;
  }

  public String getDescription() {
    return description;
  }
  

  @Override
  public void loadAll(Map<String, Object> context) {
    String[] names = {
        "label",
        "hint",
        "description",
    };
    loadNotEmpty (context, names, label, hint, description);
  }

}
