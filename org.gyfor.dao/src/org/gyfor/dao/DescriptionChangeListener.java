package org.gyfor.dao;


public interface DescriptionChangeListener {

  public void descriptionAdded (EntityDescription description);
  
  public void descriptionChanged (EntityDescription description);
  
  public void descriptionRemoved (int id);
  
}
