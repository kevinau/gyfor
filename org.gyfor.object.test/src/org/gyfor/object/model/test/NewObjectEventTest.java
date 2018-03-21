package org.gyfor.object.model.test;

import org.gyfor.object.test.data.Party;
import org.junit.Assert;
import org.junit.Test;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.PlanFactory;


public class NewObjectEventTest {
  
  private PlanFactory planFactory = new PlanFactory();
  private ModelFactory modelFactory = new ModelFactory(planFactory);
  
  private class EventCounter implements ContainerChangeListener {

    private int childAddedCount = 0;
    private int childRemovedCount = 0;
    
    @Override
    public void childAdded(IContainerModel parent, INodeModel node) {
      System.out.println("childAdded " + parent.getNodeId() + ":" + parent.getName() + " " + node.getNodeId() + ":" + node.getName());
      childAddedCount++;
    }

    @Override
    public void childRemoved(IContainerModel parent, INodeModel node) {
      childRemovedCount++;
    }
  }
  
  
  @Test
  public void testContainerEvents () {
    IEntityPlan<Party> plan = planFactory.getEntityPlan(Party.class);
    IEntityModel model = modelFactory.buildEntityModel(plan);
  
    EventCounter eventCounter = new EventCounter();
    model.addContainerChangeListener(eventCounter);

    Party party = model.newInstance();
    model.setValue(party);
    model.dump();
    
    Assert.assertEquals(7, eventCounter.childAddedCount);
    Assert.assertEquals(0, eventCounter.childRemovedCount);
  }  

}
