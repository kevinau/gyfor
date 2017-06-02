package org.gyfor.object.test;

import org.gyfor.object.Entity;
import org.gyfor.object.Id;
import org.gyfor.object.Version;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AnnotatedEntityTest {

  @Entity
  public static class SimpleEntity {

    @Id
    private int identity;
    
    @Version 
    private VersionTime versionField;
    
    private String name;

    private String location;

    @SuppressWarnings("unused")
    private EntityLife entityLife;
    
    @Override
    public String toString() {
      return name + ", " + location;
    }

  }


  private PlanFactory planFactory;
  
  
  @Before
  public void before () {
    planFactory = new PlanFactory();
  }
  
  
  @Test
  public void testBasicPlan () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    INodePlan idPlan = plan.getIdPlan();
    Assert.assertNotNull(idPlan);
    Assert.assertTrue(idPlan instanceof IItemPlan);
    Assert.assertEquals("identity", idPlan.getName());
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assert.assertNotNull(versionPlan);
    Assert.assertTrue(versionPlan instanceof IItemPlan);
    Assert.assertEquals("versionField", versionPlan.getName());
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    Assert.assertNotNull(entityLifePlan);
    Assert.assertTrue(entityLifePlan instanceof IItemPlan);
    Assert.assertEquals("entityLife", entityLifePlan.getName());
  }

}
