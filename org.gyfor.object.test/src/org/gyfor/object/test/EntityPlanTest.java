package org.gyfor.object.test;

import java.util.List;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.Optional;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.impl.EntityModel2;
import org.gyfor.object.model.impl.ItemModel;
import org.gyfor.object.model.impl.NodeModel;
import org.gyfor.object.model.impl.RootModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.ILabelGroup;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.object.plan.impl.PlanContext;
import org.gyfor.object.type.IType;
import org.gyfor.object.type.builtin.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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


  private IPlanContext planContext;
  
  @Reference
  public void setPlanContext (IPlanContext planContext) {
    this.planContext = planContext;
  }
  
  
  public void unsetPlanContext (IPlanContext planContext) {
    this.planContext = null;
  }
  

  private IEntityPlan<?> plan;
  
  
  @Before
  public void before () {
    plan = planContext.getEntityPlan(SimpleEntity.class);
  }
  
  
  @Test
  public void testBasicPlan () {
    IEntityPlan<?> plan = planContext.getEntityPlan(SimpleEntity.class);
    
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
    ILabelGroup labels = plan.getLabels();
    String title = labels.get("title");
    Assert.assertEquals("Simple entity", title);
    String shortTitle = labels.get("shortTitle");
    Assert.assertEquals("Simple entity", shortTitle);
    String description = labels.get("description");
    Assert.assertEquals("", description);
  }

  
  @Test
  public void testEntityModel () {
    RootModel root = new RootModel();
    root.addStructureChangeListener(new ContainerChangeListener() {

      @Override
      public void childAdded(INodeModel parent, INodeModel node) {
        Assert.assertTrue(parent instanceof EntityModel2);
        Assert.assertEquals("SimpleEntity", ((EntityModel2)parent).getPlan().getEntityName());
        Assert.assertTrue(node instanceof ItemModel);
      }

      @Override
      public void childRemoved(INodeModel parent, INodeModel node) {
        // TODO Auto-generated method stub
        System.out.println("child removed: " + parent + "   " + node);
      }

    });
    EntityModel2 model = root.buildEntityModel(plan);
    SimpleEntity instance = new SimpleEntity();
    instance.name = "Kevin";
    instance.location = "Australia";
    model.setValue(instance);
  }

  
  @Test
  public void testBasicModel () {
    RootModel root = new RootModel();
    
    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
    
    EntityModel2 model = root.buildEntityModel(plan, instance);

    Assert.assertEquals(1, model.getId());
    List<NodeModel> members = model.getMembers();
    Assert.assertEquals(2, members.size());
    Assert.assertTrue(model.isNameMapped());
    Assert.assertEquals(plan, model.getPlan());
    Assert.assertEquals(instance, model.getValue());
    
    INodeModel field1 = model.getMember("name");
    Assert.assertTrue(field1 instanceof ItemModel);
    Assert.assertEquals("name", ((ItemModel)field1).getName());
  }    
  
  @Test
  public void testBasicModelField1 () {
    RootModel root = new RootModel();
    
    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
    
    EntityModel2 model = root.buildEntityModel(plan, instance);
    ItemModel nameModel = model.getMember("name");
    Assert.assertEquals(2, nameModel.getId());
    Assert.assertEquals("Kevin", nameModel.getValue());
    
    IItemPlan<?> namePlan = nameModel.getPlan();
    IType<?> nameType = namePlan.getType();
    Assert.assertTrue(nameType instanceof StringType);
  }    
  
  
  @Test
  public void testBasicModelField2 () {
    RootModel root = new RootModel();
    
    SimpleEntity instance = new SimpleEntity();
    
    EntityModel2 model = root.buildEntityModel(plan, instance);
    ItemModel nameModel = model.getMember("location");
    Assert.assertEquals(3, nameModel.getId());
    Assert.assertEquals(null, nameModel.getValue());
    
    IItemPlan<?> namePlan = nameModel.getPlan();
    IType<?> nameType = namePlan.getType();
    Assert.assertTrue(nameType instanceof StringType);
  }    
  
}
