package org.gyfor.object.test;

import java.util.List;
import java.util.Map;

import org.gyfor.object.Entity;
import org.gyfor.object.Optional;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.model.INameMappedModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.type.IType;
import org.gyfor.object.type.builtin.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;

@Component
public class EntityPlanTest implements ITestClass {

  @Entity
  public static class SimpleEntity {

    private String name;

    @Optional
    private String location;

    public SimpleEntity() {
      this.name = "No name given";
      this.location = null;
    }
    

    public SimpleEntity(String name, String location) {
      this.name = name;
      this.location = location;
    }


    @Override
    public String toString() {
      return name + ", " + location;
    }

  }


  private IPlanFactory planFactory = new PlanFactory();
  private IModelFactory modelFactory = new ModelFactory();
  
  private IEntityPlan<?> plan;
  
  
  @Before
  public void before () {
    plan = planFactory.getEntityPlan(SimpleEntity.class);
  }
  
  
  @Test
  public void testBasicPlan () {
    IEntityPlan<?> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    Assert.assertNotNull(plan);
    
    INodePlan idPlan = plan.getIdPlan();
    // No id plan on this entity
    Assert.assertNull(idPlan);
    
    INodePlan versionPlan = plan.getVersionPlan();
    // No version plan on this entity
    Assert.assertNull(versionPlan);
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    // No entity life plan on this entity
    Assert.assertNull(entityLifePlan);
    
    INodePlan[] members = plan.getMemberPlans();
    Assert.assertEquals(2, members.length);
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
  public void testEntityModel () {
    IEntityModel model = modelFactory.buildEntityModel(plan);
    model.addContainerChangeListener(new ContainerChangeListener() {

      @Override
      public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> ontext) {
        Assert.assertEquals(true, parent instanceof IEntityModel);
        Assert.assertEquals("SimpleEntity", ((IEntityModel)parent).getName());
        Assert.assertEquals(true, node instanceof IItemModel);
      }

      @Override
      public void childRemoved(IContainerModel parent, INodeModel node) {
        // TODO Auto-generated method stub
      }

    });
    
    SimpleEntity instance = new SimpleEntity();
    instance.name = "Kevin";
    instance.location = "Australia";
    model.setValue(instance);
  }

  
  @Test
  public void testBasicModel () {
    IEntityModel model = modelFactory.buildEntityModel(plan);

    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
    model.setValue(instance);

    Assert.assertEquals(1, model.getNodeId());
    List<INodeModel> members = model.getMembers();
    Assert.assertEquals(2, members.size());
    Assert.assertTrue(model instanceof INameMappedModel);
    Assert.assertEquals(plan, model.getPlan());
    Assert.assertEquals(instance, model.getValue());
    
    INodeModel field1 = model.getMember("name");
    Assert.assertTrue(field1 instanceof IItemModel);
    Assert.assertEquals("name", ((IItemModel)field1).getName());
  }    
  
  @Test
  public void testBasicModelField1 () {
    IEntityModel model = modelFactory.buildEntityModel(plan);

    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
    model.setValue(instance);
    
    IItemModel nameModel = model.getMember("name");
    Assert.assertEquals(2, nameModel.getNodeId());
    Assert.assertEquals("Kevin", nameModel.getValue());
    
    IItemPlan<?> namePlan = nameModel.getPlan();
    IType<?> nameType = namePlan.getType();
    Assert.assertTrue(nameType instanceof StringType);
  }    
  
  
  @Test
  public void testBasicModelField2 () {
    IEntityModel model = modelFactory.buildEntityModel(plan);

    SimpleEntity instance = new SimpleEntity();
    model.setValue(instance);
    
    IItemModel nameModel = model.getMember("location");
    Assert.assertEquals(3, nameModel.getNodeId());
    Assert.assertEquals((String)null, nameModel.getValue());
    
    IItemPlan<?> namePlan = nameModel.getPlan();
    IType<?> nameType = namePlan.getType();
    Assert.assertTrue(nameType instanceof StringType);
  }    
  
}
