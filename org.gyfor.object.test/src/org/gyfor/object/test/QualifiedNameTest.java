package org.gyfor.object.test;

import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.PlanFactory;
import org.junit.Assert;
import org.junit.Test;


public class QualifiedNameTest {
  
  @Test
  public void testQualifiedNames() {
    PlanFactory planFactory = new PlanFactory();
    IEntityPlan<Party> entity = planFactory.getEntityPlan(Party.class);

    IItemPlan<?> namePlan = entity.selectItemPlan("name");
    Assert.assertNotNull(namePlan);
    String qname = namePlan.getQualifiedName();
    Assert.assertEquals("org.gyfor.object.test.Party#name", qname);
    
    IItemPlan<?> suburbPlan = entity.selectItemPlan("locations.suburb");
    Assert.assertNotNull(suburbPlan);
    qname = suburbPlan.getQualifiedName();
    Assert.assertEquals("org.gyfor.object.test.Party#location.suburb", qname);
  }

}
