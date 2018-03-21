package org.plcore.userio.model;

import java.util.List;

import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;
import org.plcore.userio.IEntityNode;

public interface IEntityModel extends INameMappedModel, IEntityNode {

  public <X> X newInstance();
  
  public void setValue(Object value);
  
  public void setEntityId(int id);
  
  public void setVersionTime(VersionTime versionTime);
  
  public void setEntityLife(EntityLife entityLife);
  
  public int getEntityId();
  
  public VersionTime getVersionTime();
  
  public EntityLife getEntityLife();
  
  public List<INodeModel> getDataModels();

  public void addEntityCreationListener(EntityCreationListener x);
  
  public void destroy();
  
}
