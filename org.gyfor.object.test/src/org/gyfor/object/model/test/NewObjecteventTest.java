package org.gyfor.object.model.test;

import java.util.Map;

import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.test.data.Party;
import org.junit.Assert;
import org.junit.Test;


public class NewObjecteventTest {
  
  private PlanFactory planFactory = new PlanFactory();
  private ModelFactory modelFactory = new ModelFactory(planFactory);
  
  private class EventCounter implements ContainerChangeListener {

    private int childAddedCount = 0;
    private int childRemovedCount = 0;
    
    @Override
    public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> context) {
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
    IEntityModel model = modelFactory.buildEntityModel(Party.class);
  
    EventCounter eventCounter = new EventCounter();
    model.addContainerChangeListener(eventCounter);

    Party party = model.newInstance();
    model.setValue(party);
    model.dump();
    
    Assert.assertEquals(7, eventCounter.childAddedCount);
    Assert.assertEquals(0, eventCounter.childRemovedCount);
  }  

}
