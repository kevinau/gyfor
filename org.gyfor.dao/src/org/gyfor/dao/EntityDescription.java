package org.gyfor.dao;

import org.gyfor.value.EntityLife;

public class EntityDescription implements Comparable<EntityDescription> {

  private final int id;
  
  private final String description;
  
  private final EntityLife entityLife;
  
  
  public EntityDescription (int id, String description, EntityLife entityLife) {
    this.id = id;
    if (description == null) {
      throw new IllegalArgumentException("description is null");
    }
    this.description = description;
    this.entityLife = entityLife;
  }
  
  
  public int getId () {
    return id;
  }
  
  
  public String getDescription () {
    return description;
  }

  
  public EntityLife getEntityLife () {
    return entityLife;
  }
  

  @Override
  public String toString() {
    return id + ": " + description + " " + entityLife;
  }


  @Override
  public int compareTo(EntityDescription arg) {
    return description.compareTo(arg.description);
  }
  
}
