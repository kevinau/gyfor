package org.plcore.dao;

import java.util.Objects;

import org.plcore.value.EntityLife;
import org.plcore.value.VersionTime;

public class EntityData {

  private int id;
  
  private VersionTime versionTime;
  
  private EntityLife entityLife;
  
  private Object value;
  
  public EntityData (int id, VersionTime versionTime, Object value, EntityLife entityLife) {
    this.id = id;
    this.versionTime = versionTime;
    this.value = Objects.requireNonNull(value);
    this.entityLife = entityLife;
  }

  public EntityData (int id, Object value) {
    this.id = id;
    this.versionTime = VersionTime.now();
    this.value = Objects.requireNonNull(value);
    this.entityLife = EntityLife.ACTIVE;
  }

  
  public int getId() {
    return id;
  }

  
  public void setId(int id) {
    this.id = id;
  }

  
  public VersionTime getVersionTime() {
    return versionTime;
  }

  
  public void setVersionTime(VersionTime versionTime) {
    this.versionTime = versionTime;
  }

  
  @SuppressWarnings("unchecked")
  public <X> X getValue() {
    return (X)value;
  }
  
  
  public String getClassName() {
    return value.getClass().getCanonicalName();
  }

  
  public void setValue(Object value) {
    this.value = value;
  }

  
  public EntityLife getEntityLife() {
    return entityLife;
  }

  
  public void setEntityLife(EntityLife entityLife) {
    this.entityLife = entityLife;
  }

  @Override
  public String toString() {
    return "EntityData[" + id + ", " + versionTime + ", " + entityLife + ": " + value  + "]";
  }
  
}
