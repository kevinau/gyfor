package org.gyfor.object.plan;

import java.util.Map;

import org.gyfor.object.EntityLabel;
import org.gyfor.util.CamelCase;

public class EntityLabelGroup implements ILabelGroup {

  private final String title;
  
  private final String description;
  
  public EntityLabelGroup (Class<?> klass) {
    EntityLabel labelAnn = klass.getAnnotation(EntityLabel.class);
    if (labelAnn == null) {
      title = CamelCase.toSentence(klass.getSimpleName());
      description = "";
    } else {
      String t = labelAnn.title();
      if (t.length() == 0) {
        title = CamelCase.toSentence(klass.getSimpleName());
      } else {
        title = t;
      }
      description = labelAnn.description();
    }
  }
    
    
  public static EntityLabelGroup getLabels (String className) {
    Class<?> klass;
    try {
      klass = Class.forName(className);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    return getLabels(klass);
  }
  
  
  public static EntityLabelGroup getLabels (Object instance) {
    return getLabels(instance.getClass());
  }
  
  
  public static EntityLabelGroup getLabels (Class<?> klass) {
    return new EntityLabelGroup(klass);
  }

  
  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }


  @Override
  public void extractAll(Map<String, Object> context) {
    if (title != null && title.length() > 0) {
      context.put("title", title);
    }
    if (description != null && description.length() > 0) {
      context.put("description", description);
    }
  }

}
