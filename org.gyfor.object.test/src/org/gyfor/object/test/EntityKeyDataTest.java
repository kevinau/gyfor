package org.gyfor.object.test;

import java.sql.Timestamp;
import java.util.List;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.UniqueConstraint;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.impl.PlanContext;
import org.gyfor.object.value.EntityLife;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class EntityKeyDataTest {

  @Entity
  @SuppressWarnings("unused")
  @UniqueConstraint("code")
  public static class SimpleEntity {

    private int id;
    
    private Timestamp version;
    
    private String code;
    
    private String name;

    private String location;

    private EntityLife entityLife;
    
    @Override
    public String toString() {
      return code + " " + name + ", " + location;
    }

  }


  private PlanContext context;
  
  
  @Before
  public void before () {
    context = new PlanContext();
  }
  
  
  @Test
  public void testKeys () {
    IEntityPlan<SimpleEntity> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity.class);
    
    IItemPlan<?>[] keyItems = plan.getKeyItems(0);
    Assert.assertEquals(1, keyItems.length);
    Assert.assertEquals("code", keyItems[0].getName());
  }

  
  @Test
  public void testData () {
    IEntityPlan<SimpleEntity> plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity.class);
    
    List<INodePlan> dataNodes = plan.getDataNodes(0);
    Assert.assertEquals(2, dataNodes.size());
    Assert.assertEquals("name", dataNodes.get(0).getName());
    Assert.assertEquals("location", dataNodes.get(1).getName());
  }
}
