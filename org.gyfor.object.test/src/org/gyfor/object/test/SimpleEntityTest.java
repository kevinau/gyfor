package org.gyfor.object.test;

import java.util.List;

import org.gyfor.object.Entity;
import org.gyfor.object.EntityPlanFactory;
import org.gyfor.object.Optional;
import org.gyfor.object.context.PlanFactory;
import org.gyfor.object.model.EntityModel;
import org.gyfor.object.model.ItemModel;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.NodeModel;
import org.gyfor.object.model.RootModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.ILabelGroup;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.type.IType;
import org.gyfor.object.type.builtin.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SimpleEntityTest {

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


  private PlanFactory context;
  private IEntityPlan<SimpleEntity> plan;
  
  
  @Before
  public void before () {
    context = new PlanFactory();
    plan = EntityPlanFactory.getEntityPlan(context, SimpleEntity.class);
  }
  
  
  @Test
  public void testBasicPlan () {
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
      public void childAdded(NodeModel parent, NodeModel node) {
        Assert.assertTrue(parent instanceof EntityModel);
        Assert.assertEquals("SimpleEntity", ((EntityModel)parent).getPlan().getEntityName());
        Assert.assertTrue(node instanceof ItemModel);
      }

      @Override
      public void childRemoved(NodeModel parent, NodeModel node) {
        // TODO Auto-generated method stub
        System.out.println("child removed: " + parent + "   " + node);
      }

    });
    EntityModel model = root.buildEntityModel(plan);
    SimpleEntity instance = new SimpleEntity();
    instance.name = "Kevin";
    instance.location = "Australia";
    model.setValue(instance);
  }

  
  @Test
  public void testBasicModel () {
    RootModel root = new RootModel();
    
    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
    
    EntityModel model = root.buildEntityModel(plan, instance);

    Assert.assertEquals(1, model.getId());
    List<NodeModel> members = model.getMembers();
    Assert.assertEquals(2, members.size());
    Assert.assertTrue(model.isNameMapped());
    Assert.assertEquals(plan, model.getPlan());
    Assert.assertEquals(instance, model.getValue());
    
    NodeModel field1 = model.getMember("name");
    Assert.assertTrue(field1 instanceof ItemModel);
    Assert.assertEquals("name", ((ItemModel)field1).getName());
  }    
  
  @Test
  public void testBasicModelField1 () {
    RootModel root = new RootModel();
    
    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
    
    EntityModel model = root.buildEntityModel(plan, instance);
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
    
    EntityModel model = root.buildEntityModel(plan, instance);
    ItemModel nameModel = model.getMember("location");
    Assert.assertEquals(3, nameModel.getId());
    Assert.assertEquals(null, nameModel.getValue());
    
    IItemPlan<?> namePlan = nameModel.getPlan();
    IType<?> nameType = namePlan.getType();
    Assert.assertTrue(nameType instanceof StringType);
  }    
  
}
