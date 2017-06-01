package org.gyfor.object.test;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityLabel;
import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class EntityLabelTest {

  @Entity
  public static class SimpleEntity1 {
  }


  @Entity
  @EntityLabel
  public static class SimpleEntity2 {
  }


  @Entity
  @EntityLabel(title="Simple entity one")
  public static class SimpleEntity3 {
  }


  @Entity
  @EntityLabel(title="Simple entity one", description="Simple description")
  public static class SimpleEntity4 {
  }


  private PlanFactory context;
  
  
  @Before
  public void before () {
    context = new PlanFactory();
  }
  
  
  @Test
  public void testEntityLabels1 () {
    IEntityPlan<SimpleEntity1> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity1.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity 1", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels2 () {
    IEntityPlan<SimpleEntity2> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity2.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity 2", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels3 () {
    IEntityPlan<SimpleEntity3> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity3.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity one", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels4 () {
    IEntityPlan<SimpleEntity4> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity4.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity one", title);
    String description = labels.getDescription();
    Assert.assertEquals("Simple description", description);
  }
  
  
}
