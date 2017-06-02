package org.gyfor.object.test;

import java.util.List;

import org.gyfor.object.Entity;
import org.gyfor.object.UniqueConstraint;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class EntityKeyDataTest {

  @Entity
  @SuppressWarnings("unused")
  @UniqueConstraint("code")
  public static class SimpleEntity {

    private int id;
    
    private VersionTime version;
    
    private String code;
    
    private String name;

    private String location;

    private EntityLife entityLife;
    
    @Override
    public String toString() {
      return code + " " + name + ", " + location;
    }

  }


  private PlanFactory planFactory;
  
  
  @Before
  public void before () {
    planFactory = new PlanFactory();
  }
  
  
  @Test
  public void testKeys () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    IItemPlan<?>[] keyItems = plan.getKeyItems(0);
    Assert.assertEquals(1, keyItems.length);
    Assert.assertEquals("code", keyItems[0].getName());
  }

  
  @Test
  public void testData () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    List<INodePlan> dataPlans = plan.getDataPlans();
    Assert.assertEquals(3, dataPlans.size());
    Assert.assertEquals("code", dataPlans.get(0).getName());
    Assert.assertEquals("name", dataPlans.get(1).getName());
    Assert.assertEquals("location", dataPlans.get(2).getName());
  }
}
