package org.gyfor.object.plan;

import java.sql.Timestamp;
import java.util.List;

import org.gyfor.object.value.EntityLife;


public interface IEntityPlan<T> extends IClassPlan<T> {

  public String getEntityName();

  public IItemPlan<?> getIdPlan();
  
  public int getId(Object instance);
 
  public void setId(Object instance, int id);

  public List<IItemPlan<?>[]> getUniqueConstraints();

  public String getDescription (Object instance);
  
  public IItemPlan<?> getEntityLifePlan();
  
  public EntityLife getLife(Object instance);
    
  public void setLife(Object instance, EntityLife life);

  /** 
   * Returns the version field for this entity.  If the entity does not have
   * a version field, <code>null</code> is returned.
   */
  public IItemPlan<?> getVersionPlan();
  
  public Timestamp getVersion(Object instance);
  
  public void setVersion(Object instance, Timestamp version);

  public IItemPlan<?>[] getKeyItems(int index);
  
  public List<INodePlan> getDataNodes(int index);
  
  public T newInstance();
  
  public IItemPlan<?> selectItemPlan (String path);
  
}
