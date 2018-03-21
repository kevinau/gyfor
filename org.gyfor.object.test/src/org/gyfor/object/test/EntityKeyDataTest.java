package org.gyfor.object.test;

import java.util.List;

import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.UniqueConstraint;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.PlanFactory;


@Component
public class EntityKeyDataTest {

  @Entity
  @SuppressWarnings("unused")
  @UniqueConstraint("code")
  public static class SimpleEntity {

    @IOField
    private int id;
    
    @IOField
    private VersionTime version;
    
    @IOField
    private String code;
    
    @IOField
    private String name;

    @IOField
    private String location;

    @IOField
    private EntityLife entityLife;
    
    @Override
    public String toString() {
      return code + " " + name + ", " + location;
    }

  }


  @Reference
  private PlanFactory planFactory;
  
  
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
