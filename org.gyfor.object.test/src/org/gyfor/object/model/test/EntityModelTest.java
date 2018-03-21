package org.gyfor.object.model.test;

import org.gyfor.value.EntityLife;
import org.gyfor.value.VersionTime;
import org.junit.Assert;
import org.junit.Test;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.Optional;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.PlanFactory;


public class EntityModelTest {

  @Entity
  public static class SimpleEntity {

    @IOField
    private String name;

    @IOField
    private String location;

    public SimpleEntity() {
      this.name = "No name given";
      this.location = null;
    }

    public SimpleEntity(String name, String location) {
      this.name = name;
      this.location = location;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getLocation() {
      return location;
    }

    @Optional
    public void setLocation(String location) {
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

    @IOField
    private int id;

    @IOField
    private VersionTime version;

    @IOField
    private String name;

    @IOField
    private String location;

    @IOField
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

    public int getId() {
      return id;
    }

    public VersionTime getVersion() {
      return version;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getLocation() {
      return location;
    }

    @Optional
    public void setLocation(String location) {
      this.location = location;
    }

    public EntityLife getEntityLife() {
      return entityLife;
    }

    public void setEntityLife(EntityLife entityLife) {
      this.entityLife = entityLife;
    }

    @Override
    public String toString() {
      return name + ", " + location;
    }

  }

  private PlanFactory planFactory = new PlanFactory();
  private ModelFactory modelFactory = new ModelFactory();

  @Test
  public void getSimpleEntityPlan() {
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
  public void getStandardEntityPlan() {
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
  public void createEntityModel() {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    IEntityModel model = modelFactory.buildEntityModel(plan);

    StandardEntity instance = new StandardEntity("Kevin Holloway", "Nailsworth");
    model.setValue(instance);
    StandardEntity instance2 = model.getValue();

    Assert.assertEquals("Kevin Holloway", instance2.name);
    Assert.assertEquals("Nailsworth", instance2.location);
  }

  @Test
  public void checkTopLevelEntityItems() {
    IEntityPlan<StandardEntity> plan = planFactory.getEntityPlan(StandardEntity.class);
    IEntityModel entityModel = modelFactory.buildEntityModel(plan);

    StandardEntity instance = new StandardEntity(123, "Kevin Holloway", "Nailsworth");
    entityModel.setValue(instance);

    INodeModel[] children = entityModel.getMembers();
    Assert.assertEquals(5, children.length);

    for (INodeModel child : children) {
      Assert.assertTrue(child instanceof IItemModel);
      System.out.println("zzzzzzzzzzz " + child.getName());
    }

    IItemModel idModel = entityModel.getMember("id");
    int id = idModel.getValue();
    Assert.assertEquals(123, id);

    IItemModel versionModel = entityModel.getMember("version");
    VersionTime version = versionModel.getValue();
    Assert.assertEquals(version, instance.version);

    IItemModel nameModel = entityModel.getMember("name");
    String name = nameModel.getValue();
    Assert.assertEquals("Kevin Holloway", name);

    IItemModel locationModel = entityModel.getMember("location");
    String location = locationModel.getValue();
    Assert.assertEquals("Nailsworth", location);

    IItemModel entityLifeModel = entityModel.getMember("entityLife");
    EntityLife entityLife = entityLifeModel.getValue();
    Assert.assertEquals(EntityLife.ACTIVE, entityLife);
  }

}
