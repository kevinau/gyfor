package org.gyfor.object.test;

import org.gyfor.object.Entity;
import org.gyfor.object.Optional;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.VersionTime;
import org.junit.Assert;
import org.junit.Test;


public class EntityModelTest {

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
  
  
  @SuppressWarnings("unused")
  @Entity
  public static class StandardEntity {

    private int id;
    
    private VersionTime version;
    
    private String name;

    @Optional
    private String location;

    private EntityLife entityLife;
    
    
    public StandardEntity() {
      this.name = "No name given";
      this.location = null;
    }
    

    public StandardEntity(String name, String location) {
      this.name = name;
      this.location = location;
    }


    public StandardEntity(int id, String name, String location) {
      this.id = id;
      this.version = VersionTime.now();
      this.name = name;
      this.location = location;
      this.entityLife = EntityLife.ACTIVE;
    }


    @Override
    public String toString() {
      return name + ", " + location;
    }

  }

  
  private PlanFactory planFactory = new PlanFactory();
  private ModelFactory modelFactory = new ModelFactory();
  
  @Test
  public void getSimpleEntityPlan () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    INodePlan idPlan = plan.getIdPlan();
    // No id plan on this entity
    Assert.assertNull(idPlan);
    
    INodePlan versionPlan = plan.getVersionPlan();
    // No version plan on this entity
    Assert.assertNull(versionPlan);
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    // No entity life plan on this entity
    Assert.assertNull(entityLifePlan);
    
    INodePlan[] members = plan.getMembers();
    Assert.assertEquals(2, members.length);
  }
  
  @Test
  public void getStandardEntityPlan () {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    INodePlan idPlan = plan.getIdPlan();
    // id plan exists for this entity
    Assert.assertNotNull(idPlan);
    
    INodePlan versionPlan = plan.getVersionPlan();
    // version plan exists for this entity
    Assert.assertNotNull(versionPlan);
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    // entity life plan exists for this entity
    Assert.assertNotNull(entityLifePlan);
    
    INodePlan[] members = plan.getMembers();
    Assert.assertEquals(5, members.length);
  }
  
  
  @Test 
  public void createEntityModel () {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    StandardEntity instance = new StandardEntity("Kevin Holloway", "Nailsworth");
    model.setValue(instance);
    StandardEntity instance2 = model.getValue();
    
    Assert.assertEquals("Kevin Holloway", instance2.name);
    Assert.assertEquals("Nailsworth", instance2.location);
  }

  
  @Test 
  public void checkTopLevelEntityItems () {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    IEntityModel entityModel = modelFactory.buildEntityModel(plan);
    
    StandardEntity instance = new StandardEntity(123, "Kevin Holloway", "Nailsworth");
    entityModel.setValue(instance);

    INodeModel[] children = entityModel.getMembers();
    Assert.assertEquals(5, children.length);
    
    for (INodeModel child : children) {
      Assert.assertTrue(child instanceof IItemModel);
    }
 
    IItemModel idModel = (IItemModel)children[0];
    int id = idModel.getValue();
    Assert.assertEquals(123, id);

    IItemModel versionModel = (IItemModel)children[1];
    VersionTime version = versionModel.getValue();
    Assert.assertEquals(version, instance.version);

    IItemModel nameModel = (IItemModel)children[2];
    String name = nameModel.getValue();
    Assert.assertEquals("Kevin Holloway", name);

    IItemModel locationModel = (IItemModel)children[3];
    String location = locationModel.getValue();
    Assert.assertEquals("Nailsworth", location);

    IItemModel entityLifeModel = (IItemModel)children[4];
    EntityLife entityLife = entityLifeModel.getValue();
    Assert.assertEquals(EntityLife.ACTIVE, entityLife);
  }
}
