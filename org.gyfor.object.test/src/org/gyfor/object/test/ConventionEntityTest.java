package org.gyfor.object.test;

import java.sql.Timestamp;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.context.PlanFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.value.EntityLife;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ConventionEntityTest {

  @Entity
  @SuppressWarnings("unused")
  public static class SimpleEntity {

    private int id;
    
    private Timestamp version;
    
    private String name;

    private String location;

    private EntityLife entityLife;
    
    @Override
    public String toString() {
      return name + ", " + location;
    }

  }


  private PlanFactory context;
  
  
  @Before
  public void before () {
    context = new PlanFactory();
  }
  
  
  @Test
  public void testBasicPlan () {
    IEntityPlan<SimpleEntity> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity.class);
    
    INodePlan idPlan = plan.getIdPlan();
    Assert.assertNotNull(idPlan);
    Assert.assertTrue(idPlan instanceof IItemPlan);
    Assert.assertEquals("id", idPlan.getName());
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assert.assertNotNull(versionPlan);
    Assert.assertTrue(versionPlan instanceof IItemPlan);
    Assert.assertEquals("version", versionPlan.getName());
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    Assert.assertNotNull(entityLifePlan);
    Assert.assertTrue(entityLifePlan instanceof IItemPlan);
    Assert.assertEquals("entityLife", entityLifePlan.getName());
  }

}