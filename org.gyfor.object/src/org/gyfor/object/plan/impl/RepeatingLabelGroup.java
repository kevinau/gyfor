package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;
import java.util.Map;

import org.gyfor.object.NumberedIndexLabelProvider;
import org.gyfor.object.RepeatingLabel;
import org.gyfor.object.plan.IIndexLabelProvider;
import org.gyfor.object.plan.ILabelGroup;
import org.gyfor.util.CamelCase;


public class RepeatingLabelGroup implements ILabelGroup {

  private final String label;

  private final String description;

  private final IIndexLabelProvider indexLabelProvider;


  public RepeatingLabelGroup() {
    this("", "");
  }


  public RepeatingLabelGroup(String label, String description) {
    this.label = label;
    this.description = description;
    this.indexLabelProvider = new NumericIndexLabelProvider();
  }


  public RepeatingLabelGroup(Field field, String fieldName) {
    RepeatingLabel labelAnn = field.getAnnotation(RepeatingLabel.class);
    if (labelAnn == null) {
      label = CamelCase.toSentence(fieldName);
      description = "";
      indexLabelProvider = new NumberedIndexLabelProvider();
    } else {
      if (labelAnn.value().equals("\u0000")) {
        label = CamelCase.toSentence(fieldName);
      } else {
        label = labelAnn.value();
      }
      Class<? extends IIndexLabelProvider> labelClass = labelAnn.indexLabels();
      try {
        indexLabelProvider = labelClass.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new RuntimeException("Index label provider '" + labelClass.getName() + "' not found", ex);
      }
      description = labelAnn.description();
    }
  }


  public String getLabel() {
    return label;
  }


  public String getDescription() {
    return description;
  }


  public String getIndexLabel(int index) {
    return indexLabelProvider.getIndexLabel(index);
  }


  @Override
  public void loadAll(Map<String, Object> context) {
    String[] names = {
        "label",
        "description",
    };
    loadNotEmpty(context, names, label, description);
  }

}
