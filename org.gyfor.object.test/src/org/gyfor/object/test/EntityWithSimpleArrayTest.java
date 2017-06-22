package org.gyfor.object.test;

import java.util.List;

import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IRepeatingPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.test.data.EntityWithSimpleArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;

@Component
public class EntityWithSimpleArrayTest {

  private PlanFactory planFactory = new PlanFactory();
  
  private IEntityPlan<?> plan;
  
  
  @Before
  public void before () {
    plan = planFactory.getEntityPlan(EntityWithSimpleArray.class);
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
    Assert.assertEquals("Entity with simple array", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }

  
  @Test
  public void testNodePaths () {
    IItemPlan<?> field1Plan = plan.selectItemPlan("field1");
    Assert.assertEquals("field1", field1Plan.getName());
    
    INodePlan innerPlan = plan.selectNodePlan("field2");
    Assert.assertEquals("field2", innerPlan.getName());
    Assert.assertEquals(true, innerPlan instanceof IRepeatingPlan);
    
    IItemPlan<?> innerField1Plan = plan.selectItemPlan("field2[]");
    Assert.assertEquals("field2", innerField1Plan.getName());
    
    List<INodePlan> plans = plan.selectNodePlans("..");
    Assert.assertEquals(3, plans.size());
    
    List<IItemPlan<?>> plans2 = plan.selectItemPlans("..");
    Assert.assertEquals(2, plans2.size());
  }

}
