package org.gyfor.object.test;

import java.util.List;

import org.gyfor.object.test.data.SimpleEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;
import org.plcore.userio.plan.EntityLabelGroup;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.PlanFactory;

@Component
public class SimpleEntityTest {

  private PlanFactory planFactory = new PlanFactory();
//  private ModelFactory modelFactory = new ModelFactory();
  
  private IEntityPlan<?> plan;
  
  
  @Before
  public void before () {
    plan = planFactory.getEntityPlan(SimpleEntity.class);
  }
  
  
  @Test
  public void testBasicPlan () {    
    INodePlan idPlan = plan.getIdPlan();
    Assert.assertNotNull(idPlan);
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assert.assertNotNull(versionPlan);
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    Assert.assertNotNull(entityLifePlan);
    
    List<INodePlan> dataPlans = plan.getDataPlans();
    Assert.assertEquals(2, dataPlans.size());
  }
  

  @Test
  public void testEntityLabels () {
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assert.assertEquals("Simple entity", title);
    String description = labels.getDescription();
    Assert.assertEquals("", description);
  }

  
  @Test
  public void testNodePaths () {
    IItemPlan<?> field1Plan = plan.selectItemPlan("field1");
    Assert.assertEquals("field1", field1Plan.getName());
    
    IItemPlan<?> field2Plan = plan.selectItemPlan("field2");
    Assert.assertEquals("field2", field2Plan.getName());

    List<IItemPlan<?>> plans = plan.selectItemPlans("*");
    Assert.assertEquals(5, plans.size());
  }
  
//  @Test
//  public void testEntityModel () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//    model.addContainerChangeListener(new ContainerChangeListener() {
//
//      @Override
//      public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> ontext) {
//        Assert.assertEquals(true, parent instanceof IEntityModel);
//        Assert.assertEquals("SimpleEntity", ((IEntityModel)parent).getName());
//        Assert.assertEquals(true, node instanceof IItemModel);
//      }
//
//      @Override
//      public void childRemoved(IContainerModel parent, INodeModel node) {
//        // TODO Auto-generated method stub
//      }
//
//    });
//    
//    SimpleEntity instance = new SimpleEntity();
//    instance.name = "Kevin";
//    instance.location = "Australia";
//    model.setValue(instance);
//  }
//
//  
//  @Test
//  public void testBasicModel () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//
//    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
//    model.setValue(instance);
//
//    Assert.assertEquals(1, model.getNodeId());
//    INodeModel[] members = model.getMembers();
//    Assert.assertEquals(2, members.length);
//    Assert.assertTrue(model instanceof INameMappedModel);
//    Assert.assertEquals(plan, model.getPlan());
//    Assert.assertEquals(instance, model.getValue());
//    
//    INodeModel field1 = model.getMember("name");
//    Assert.assertTrue(field1 instanceof IItemModel);
//    Assert.assertEquals("name", ((IItemModel)field1).getName());
//  }    
//  
//  @Test
//  public void testBasicModelField1 () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//
//    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
//    model.setValue(instance);
//    
//    IItemModel nameModel = model.getMember("name");
//    Assert.assertEquals(2, nameModel.getNodeId());
//    Assert.assertEquals("Kevin", nameModel.getValue());
//    
//    IItemPlan<?> namePlan = nameModel.getPlan();
//    IType<?> nameType = namePlan.getType();
//    Assert.assertTrue(nameType instanceof StringType);
//  }    
//  
//  
//  @Test
//  public void testBasicModelField2 () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//
//    SimpleEntity instance = new SimpleEntity();
//    model.setValue(instance);
//    
//    IItemModel nameModel = model.getMember("location");
//    Assert.assertEquals(3, nameModel.getNodeId());
//    Assert.assertEquals((String)null, nameModel.getValue());
//    
//    IItemPlan<?> namePlan = nameModel.getPlan();
//    IType<?> nameType = namePlan.getType();
//    Assert.assertTrue(nameType instanceof StringType);
//  }    
  
}
