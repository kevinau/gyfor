package org.plcore.dao;

import org.plcore.entity.EntityLife;

public class EntityReference implements Comparable<EntityReference> {

  private final int id;
  
  private final String description;
  
  private final EntityLife entityLife;
  
  
  public EntityReference (int id, String description, EntityLife entityLife) {
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
  public int compareTo(EntityReference arg) {
    int n = description.compareTo(arg.description);
    if (n != 0) {
      return n;
    } else {
      return id - arg.id;
    }
  }
  
}
