package org.gyfor.dao;

import java.util.List;

import org.gyfor.object.value.EntityDescription;

public interface IEntitySet {
  
  public List<EntityDescription> getAllDescriptions();

  public void addDescriptionChangeListener (DescriptionChangeListener x);
 
  public void removeDescriptionChangeListener (DescriptionChangeListener x);

}
