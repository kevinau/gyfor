package org.gyfor.object.plan;

import java.util.Map;

import org.gyfor.object.EntityLabel;
import org.gyfor.util.CamelCase;

public class EntityLabelGroup implements ILabelGroup {

  private final String shortTitle;
  
  private final String title;
  
  private final String description;
  
  public EntityLabelGroup (Class<?> klass) {
    EntityLabel labelAnn = klass.getAnnotation(EntityLabel.class);
    if (labelAnn == null) {
      shortTitle = CamelCase.toSentence(klass.getSimpleName());
      title = shortTitle;
      description = "";
    } else {
      String st = labelAnn.shortTitle();
      if (st.length() == 0) {
        shortTitle = CamelCase.toSentence(klass.getSimpleName());
      } else {
        shortTitle = st;
      }
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

  
  public String getShortTitle() {
    return shortTitle;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }


  @Override
  public void loadAll(Map<String, Object> context) {
    String[] names = {
        "shortTitle",
        "title",
        "description",
    };
    loadNotEmpty (context, names, shortTitle, title, description);
  }

}
