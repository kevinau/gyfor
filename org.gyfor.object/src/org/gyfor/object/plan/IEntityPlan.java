package org.gyfor.object.plan;

import java.sql.Timestamp;
import java.util.List;

import org.gyfor.object.value.EntityDescription;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;
import org.gyfor.sql.IResultSet;


public interface IEntityPlan<T> extends IClassPlan<T> {

  public String getEntityName();

  public IItemPlan<Integer> getIdPlan();
  
  public boolean hasId();
  
  public int getId(Object instance);
 
  public void setId(Object instance, int id);

  public List<IItemPlan<?>[]> getUniqueConstraints();

  public EntityDescription getDescription (Object instance);
  
  public IItemPlan<EntityLife> getEntityLifePlan();
  
  public boolean hasEntityLife();
  
  public EntityLife getEntityLife(Object instance);
    
  public void setEntityLife(Object instance, EntityLife life);

  /** 
   * Returns the version field for this entity.  If the entity does not have
   * a version field, <code>null</code> is returned.
   */
  public IItemPlan<VersionTime> getVersionPlan();
  
  public boolean hasVersion ();
  
  public Timestamp getVersion(Object instance);
  
  public void setVersion(Object instance, Timestamp version);

  public IItemPlan<?>[] getKeyItems(int index);
  
  @Override
  public <X> X newInstance();
  
  public T newInstance(IItemPlan<?>[] sqlPlans, IResultSet rs);
  
  public List<INodePlan> getDataPlans();

  public List<IItemPlan<?>> getDescriptionPlans();

}
