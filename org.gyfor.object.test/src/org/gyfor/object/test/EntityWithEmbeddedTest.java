package org.gyfor.object.test;

import java.util.List;

import org.gyfor.object.test.data.EntityWithEmbedded;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;
import org.plcore.userio.plan.EntityLabelGroup;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.PlanFactory;

@Component
public class EntityWithEmbeddedTest {

  private PlanFactory planFactory = new PlanFactory();
  
  private IEntityPlan<?> plan;
  
  
  @Before
  public void before () {
    plan = planFactory.getEntityPlan(EntityWithEmbedded.class);
  }
  
  
  @Test
  public void testBasicPlan () {
    INodePlan idPlan = plan.getIdPlan();
    Assert.assertNotNull(idPlan);
    
    List<INodePlan> dataPlans = plan.getDataPlans();
    Assert.assertEquals(2, dataPlans.size());
  }
  

  @Test
  public void testEntityLabels () {
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Entity with embedded", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }

  
  @Test
  public void testNodePaths () {
    IItemPlan<?> field1Plan = plan.selectItemPlan("field1");
    Assert.assertEquals("field1", field1Plan.getName());
    
    INodePlan innerPlan = plan.selectNodePlan("inner");
    Assert.assertEquals("inner", innerPlan.getName());
    Assert.assertEquals(true, innerPlan instanceof IEmbeddedPlan);
    
    IItemPlan<?> innerField1Plan = plan.selectItemPlan("inner/field1");
    Assert.assertEquals("field1", innerField1Plan.getName());
    
    IItemPlan<?> innerField2Plan = plan.selectItemPlan("inner/field2");
    Assert.assertEquals("field2", innerField2Plan.getName());
    
    List<IItemPlan<?>> plans = plan.selectItemPlans("**");
    Assert.assertEquals(4, plans.size());
  }

}
