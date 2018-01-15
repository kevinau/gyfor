package org.gyfor.object.model.test;

import org.gyfor.object.Entity;
import org.gyfor.object.Optional;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;
import org.junit.Assert;
import org.junit.Test;


public class ContainerEventsTest {
  @SuppressWarnings("unused")
  @Entity
  public static class StandardEntity {

    private int id;
    
    private VersionTime version;
    
    private String name;

    @Optional
    private String location;

    private EntityLife entityLife;
    
    
    public StandardEntity() {
      this.name = "No name given";
      this.location = null;
    }
    

    public StandardEntity(String name, String location) {
      this.name = name;
      this.location = location;
    }


    public StandardEntity(int id, String name, String location) {
      this.id = id;
      this.version = VersionTime.now();
      this.name = name;
      this.location = location;
      this.entityLife = EntityLife.ACTIVE;
    }


    @Override
    public String toString() {
      return name + ", " + location;
    }

  }

  
  private PlanFactory planFactory = new PlanFactory();
  private ModelFactory modelFactory = new ModelFactory();
  
  private class EventCounter implements ContainerChangeListener {

    private int childAddedCount = 0;
    private int childRemovedCount = 0;
    
    @Override
    public void childAdded(IContainerModel parent, INodeModel node) {
      childAddedCount++;
    }

    @Override
    public void childRemoved(IContainerModel parent, INodeModel node) {
      childRemovedCount++;
    }
    
  }
  
  
  @Test
  public void testContainerEvents () {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    IEntityModel model = modelFactory.buildEntityModel(plan);
  
    EventCounter eventCounter = new EventCounter();
    model.addContainerChangeListener(eventCounter);

    model.setValue(null);
    Assert.assertEquals(0, eventCounter.childAddedCount);
    Assert.assertEquals(0, eventCounter.childRemovedCount);

    StandardEntity instance = new StandardEntity(123, "Kevin Holloway", "Nailsworth");
    model.setValue(instance);
    Assert.assertEquals(5, eventCounter.childAddedCount);
    Assert.assertEquals(0, eventCounter.childRemovedCount);
    
    model.setValue(null);
    Assert.assertEquals(5, eventCounter.childAddedCount);
    Assert.assertEquals(5, eventCounter.childRemovedCount);
  }  

}
