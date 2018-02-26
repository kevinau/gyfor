package org.gyfor.object.model.test;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.EffectiveEntryMode;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.test.data.ModeTestEntity;
import org.gyfor.object.test.data.OuterModeTestEntity;
import org.junit.Assert;
import org.junit.Test;


public class EntryModeTest {

  @Test
  public void simplePlanAnnotations () {
    PlanFactory planFactory = new PlanFactory();
    IEntityPlan<?> plan = planFactory.getEntityPlan(ModeTestEntity.class);
   
    IItemPlan<?> field0 = plan.selectItemPlan("field0");
    Assert.assertEquals(EntryMode.UNSPECIFIED, field0.getEntryMode());

    IItemPlan<?> field1 = plan.selectItemPlan("field1");
    Assert.assertEquals(EntryMode.ENABLED, field1.getEntryMode());
    
    IItemPlan<?> field2 = plan.selectItemPlan("field2");
    Assert.assertEquals(EntryMode.DISABLED, field2.getEntryMode());
    
    IItemPlan<?> field3 = plan.selectItemPlan("field3");
    Assert.assertEquals(EntryMode.VIEW, field3.getEntryMode());
    
    IItemPlan<?> field4 = plan.selectItemPlan("field4");
    Assert.assertEquals(EntryMode.HIDDEN, field4.getEntryMode());
  }
  
  
  @Test
  public void simpleModeAnnotations () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(ModeTestEntity.class);
    model.setValue(new ModeTestEntity());
    
    IItemModel field0 = model.selectItemModel("field0");
    Assert.assertEquals(EffectiveEntryMode.ENABLED, field0.getEffectiveEntryMode());

    IItemModel field1 = model.selectItemModel("field1");
    Assert.assertEquals(EffectiveEntryMode.ENABLED, field1.getEffectiveEntryMode());
    
    IItemModel field2 = model.selectItemModel("field2");
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field2.getEffectiveEntryMode());
    
    IItemModel field3 = model.selectItemModel("field3");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    
    IItemModel field4 = model.selectItemModel("field4");
    Assert.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
  }
  
  
  @Test
  public void inheritedMode () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(OuterModeTestEntity.class);
    model.setValue(new OuterModeTestEntity());
    
    INodeModel fieldx = model.selectNodeModel("inner");
    Assert.assertEquals(EffectiveEntryMode.VIEW, fieldx.getEffectiveEntryMode());

    IItemModel field0 = model.selectItemModel("inner/field0");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field0.getEffectiveEntryMode());

    IItemModel field1 = model.selectItemModel("inner/field1");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field1.getEffectiveEntryMode());
    
    IItemModel field2 = model.selectItemModel("inner/field2");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field2.getEffectiveEntryMode());
    
    IItemModel field3 = model.selectItemModel("inner/field3");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    
    IItemModel field4 = model.selectItemModel("inner/field4");
    Assert.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
    
    fieldx.setEntryMode(EntryMode.DISABLED);
    Assert.assertEquals(EffectiveEntryMode.DISABLED, fieldx.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field0.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field1.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field2.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
  }
}
