package org.gyfor.object.test;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityLabel;
import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.context.PlanEnvironment;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.ILabelGroup;
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
  @EntityLabel(title="Simple entity one", shortTitle="Simple one", description="Simple description")
  public static class SimpleEntity4 {
  }


  private PlanEnvironment context;
  
  
  @Before
  public void before () {
    context = new PlanEnvironment();
  }
  
  
  @Test
  public void testEntityLabels1 () {
    IEntityPlan<SimpleEntity1> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity1.class);
    ILabelGroup labels = plan.getLabels();
    String title = labels.get("title");
    Assert.assertEquals("Simple entity 1", title);
    String shortTitle = labels.get("shortTitle");
    Assert.assertEquals("Simple entity 1", shortTitle);
    String description = labels.get("description");
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels2 () {
    IEntityPlan<SimpleEntity2> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity2.class);
    ILabelGroup labels = plan.getLabels();
    String title = labels.get("title");
    Assert.assertEquals("Simple entity 2", title);
    String shortTitle = labels.get("shortTitle");
    Assert.assertEquals("Simple entity 2", shortTitle);
    String description = labels.get("description");
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels3 () {
    IEntityPlan<SimpleEntity3> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity3.class);
    ILabelGroup labels = plan.getLabels();
    String title = labels.get("title");
    Assert.assertEquals("Simple entity one", title);
    String shortTitle = labels.get("shortTitle");
    Assert.assertEquals("Simple entity 3", shortTitle);
    String description = labels.get("description");
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels4 () {
    IEntityPlan<SimpleEntity4> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity4.class);
    ILabelGroup labels = plan.getLabels();
    String title = labels.get("title");
    Assert.assertEquals("Simple entity one", title);
    String shortTitle = labels.get("shortTitle");
    Assert.assertEquals("Simple one", shortTitle);
    String description = labels.get("description");
    Assert.assertEquals("Simple description", description);
  }
  
  
}
