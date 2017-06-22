package org.gyfor.object.model.test;

import org.gyfor.object.Entity;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.ItemEventListener;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.junit.Assert;
import org.junit.Test;


public class ItemEventsTest {
  @SuppressWarnings("unused")
  @Entity
  public static class StandardEntity {

    private int field1;
    
    public StandardEntity() {
      this.field1 = 123;
    }
    

    public StandardEntity(int field1) {
      this.field1 = field1;
    }
  }

  
  private PlanFactory planFactory = new PlanFactory();
  private ModelFactory modelFactory = new ModelFactory();
  
  private class EventCounter implements ItemEventListener {

    private int valueChangeCount = 0;
    //private int valueEqualityChangeCount = 0;
    private int sourceChangeCount = 0;
    //private int sourceEqualityChangeCount = 0;
    private int errorNoted = 0;
    private int errorCleared = 0;
    //private UserEntryException lastError;
    
    
    @Override
    public String getOrigin() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void valueEqualityChange(IItemModel model) {
      //valueEqualityChangeCount++;
    }

    @Override
    public void sourceEqualityChange(IItemModel model) {
      //sourceEqualityChangeCount++;
    }

    @Override
    public void valueChange(IItemModel model) {
      valueChangeCount++;
    }

    @Override
    public void errorCleared(IItemModel model) {
      errorCleared++;
    }

    @Override
    public void errorNoted(IItemModel model, UserEntryException ex) {
      errorNoted++;
      //lastError = ex;
    }

    @Override
    public void sourceChange(IItemModel model) {
      sourceChangeCount++;
    }

    @Override
    public void comparisonBasisChange(IItemModel model) {
      // TODO Auto-generated method stub
    }
    
  }
  
  
  @Test
  public void testItemEvents () {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    StandardEntity instance = new StandardEntity();
    model.setValue(instance);
    
    IItemModel itemModel = model.getMember("field1");  
    Assert.assertNotNull(itemModel);

    EventCounter eventCounter = new EventCounter();
    itemModel.addItemEventListener(eventCounter);

    itemModel.setValue(234);
    Assert.assertEquals(1, eventCounter.valueChangeCount);
    Assert.assertEquals(1, eventCounter.sourceChangeCount);

    itemModel.setValueFromSource("345");
    Assert.assertEquals(2, eventCounter.valueChangeCount);
    Assert.assertEquals(2, eventCounter.sourceChangeCount);

    itemModel.setValueFromSource("0345");
    Assert.assertEquals(2, eventCounter.valueChangeCount);
    Assert.assertEquals(3, eventCounter.sourceChangeCount);
    Assert.assertEquals(0, eventCounter.errorNoted);
}  

  @Test
  public void testItemErrors () {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    StandardEntity instance = new StandardEntity();
    model.setValue(instance);
    
    IItemModel itemModel = model.getMember("field1");  
    Assert.assertNotNull(itemModel);

    EventCounter eventCounter = new EventCounter();
    itemModel.addItemEventListener(eventCounter);

    itemModel.setValueFromSource("ABCD");
    Assert.assertEquals(1, eventCounter.errorNoted);

    itemModel.setValueFromSource("1234");
    Assert.assertEquals(1, eventCounter.errorNoted);
    Assert.assertEquals(1, eventCounter.errorCleared);
}  

}
