package org.gyfor.object.test;

import org.gyfor.object.Embedded;
import org.gyfor.object.Entity;
import org.gyfor.object.model.impl2.IEntityModel;
import org.gyfor.object.model.impl2.IItemModel;
import org.gyfor.object.model.impl2.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.value.VersionTime;
import org.junit.Assert;
import org.junit.Test;


public class EntityEmbeddedModelTest {

  public static class Location {
    private String street;
    
    private String suburb;
    
    public Location () {
      this ("No street", "No suburb");
    }
    
    public Location (String street, String suburb) {
      this.street = street;
      this.suburb = suburb;
    }


    @Override
    public String toString() {
      return street + ", " + suburb;
    }

  }
  
  @Entity
  public static class Party {

    @SuppressWarnings("unused")
    private int id;
    
    @SuppressWarnings("unused")
    private VersionTime version;
    
    private String name;

    @Embedded
    private Location location;

    public Party() {
      this.name = "No name given";
      this.location = new Location();
    }
    

    public Party(String name, String street, String suburb) {
      this.name = name;
      this.location = new Location(street, suburb);
    }


    @Override
    public String toString() {
      return name + ", " + location;
    }

  }
  
  
  private PlanFactory planFactory = new PlanFactory();
  private ModelFactory modelFactory = new ModelFactory();
  
//  @Test
  public void getPartyPlan () {
    IEntityPlan<Party> plan = planFactory.getEntityPlan(Party.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    INodePlan idPlan = plan.getIdPlan();
    Assert.assertNotNull(idPlan);
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assert.assertNotNull(versionPlan);
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    // No entity life plan on this entity
    Assert.assertNull(entityLifePlan);
    
    INodePlan[] members = plan.getMemberPlans();
    // 4 members: id, version, name, location
    Assert.assertEquals(4, members.length);
  }
  
  @Test 
  public void createEntityModel () {
    IEntityPlan<Party> plan = planFactory.getEntityPlan(Party.class);
    IEntityModel model = modelFactory.buildEntityModel(plan);
    System.out.println("1-------------------");
    model.dump(0);
    System.out.println("-------------------");
    
    Party instance = new Party("Kevin Holloway", "Burwood Avenue", "Nailsworth");
    model.setValue(instance);
    System.out.println("2-------------------");
    model.dump(0);
    System.out.println("-------------------");
    
    Party instance2 = model.getValue();
    
    Assert.assertEquals("Kevin Holloway", instance2.name);
    Assert.assertEquals("Burwood Avenue", instance2.location.street);
    Assert.assertEquals("Nailsworth", instance2.location.suburb);
  }

  
//  @Test 
  public void checkMemberItems () {
    IEntityPlan<Party> plan = planFactory.getEntityPlan(Party.class);
    IEntityModel entityModel = modelFactory.buildEntityModel(plan);
    
    Party instance = new Party("Kevin Holloway", "Burwood Avenue", "Nailsworth");
    entityModel.setValue(instance);

    IItemModel nameModel = entityModel.selectItemModel("name");
    Assert.assertEquals("Kevin Holloway", nameModel.getValue());

    IItemModel suburbModel = entityModel.selectItemModel("location.suburb");
    Assert.assertEquals("Nailsworth", suburbModel.getValue());
  }
}
