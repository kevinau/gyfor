package org.gyfor.object.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.plcore.userio.Entity;
import org.plcore.userio.EntityLabel;
import org.plcore.userio.plan.EntityLabelGroup;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.PlanFactory;


public class EntityLabelTest {

  @Entity
  public static class SimpleEntity1 {
  }


  @Entity
  @EntityLabel
  public static class SimpleEntity2 {
  }


  @Entity
  @EntityLabel(value="Simple entity one")
  public static class SimpleEntity3 {
  }


  @Entity
  @EntityLabel(value="Simple entity one", description="Simple description")
  public static class SimpleEntity4 {
  }


  private PlanFactory planFactory;
  
  
  @Before
  public void before () {
    planFactory = new PlanFactory();
  }
  
  
  @Test
  public void testEntityLabels1 () {
    IEntityPlan<SimpleEntity1> plan = planFactory.getEntityPlan(SimpleEntity1.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity 1", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels2 () {
    IEntityPlan<SimpleEntity2> plan = planFactory.getEntityPlan(SimpleEntity2.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity 2", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels3 () {
    IEntityPlan<SimpleEntity3> plan = planFactory.getEntityPlan(SimpleEntity3.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity one", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }
  
  
  @Test
  public void testEntityLabels4 () {
    IEntityPlan<SimpleEntity4> plan = planFactory.getEntityPlan(SimpleEntity4.class);
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity one", title);
    String description = labels.getDescription();
    Assert.assertEquals("Simple description", description);
  }
  
  
}
