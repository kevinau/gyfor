package org.gyfor.object.test;

import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.plcore.userio.Entity;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.PlanFactory;


public class SpecialEntityFieldsByConvention {

  @Entity
  @SuppressWarnings("unused")
  public static class SimpleEntity {

    private int id;
    
    private VersionTime version;
    
    private String name;

    private String location;

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
  public void entityPlanByConvention () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
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
