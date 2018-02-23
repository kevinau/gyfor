package org.gyfor.object.model.test;

import org.gyfor.object.Entity;
import org.gyfor.object.IOField;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.type.builtin.EntityLifeType;
import org.gyfor.value.EntityLife;
import org.junit.Assert;
import org.junit.Test;


public class BuiltinTypeTest {

  @SuppressWarnings("unused")
  @Entity
  public static class PrimitivesEntity {
    @IOField
    private boolean field1;

    @IOField
    private char field2;

    @IOField
    private byte field3;

    @IOField
    private short field4;

    @IOField
    private int field5;

    @IOField
    private long field6;

    @IOField
    private float field7;

    @IOField
    private double field8;

    public PrimitivesEntity(boolean field1, char field2, byte field3, short field4, int field5, long field6,
        float field7, double field8) {
      this.field1 = field1;
      this.field2 = field2;
      this.field3 = field3;
      this.field4 = field4;
      this.field5 = field5;
      this.field6 = field6;
      this.field7 = field7;
      this.field8 = field8;
    }

  }
  
  
  private PlanFactory planFactory = new PlanFactory();
  
  
  //@Test
  public void primitivesEntityModel () {
    IEntityPlan<PrimitivesEntity> plan = planFactory.getEntityPlan(PrimitivesEntity.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    ModelFactory modelFactory = new ModelFactory();
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    PrimitivesEntity instance = new PrimitivesEntity(true, 'A', (byte)123, (short)1234, 12345, 123456L, 12.34F, 1234.5678);
    model.setValue(instance);
    
    INodeModel[] items = model.getMembers();
    Assert.assertEquals(8,  items.length);
    for (int i = 0; i < 8; i++) {
      Assert.assertTrue(items[i] instanceof IItemModel);
      IItemModel itemModel = (IItemModel)items[i];
      Assert.assertEquals(true, itemModel.getType().isPrimitive());
      Assert.assertEquals(false, itemModel.getType().isNullable());
    }
    Object[] values = new Object[8];
    for (int i = 0; i < 8; i++) {
      values[i] = items[i].getValue();
    }
    Assert.assertEquals(true, values[0]);
    Assert.assertEquals('A', values[1]);
    Assert.assertEquals((byte)123, values[2]);
    Assert.assertEquals((short)1234, values[3]);
    Assert.assertEquals(12345, values[4]);
    Assert.assertEquals(123456L, values[5]);
    Assert.assertEquals(12.34F, values[6]);
    Assert.assertEquals(1234.5678, values[7]);
  }
  
  
  @Entity
  public static class EntityLifeEntity {
    @IOField
    private EntityLife field1;

    public EntityLifeEntity(EntityLife field1) {
      this.field1 = field1;
    }
   
    public EntityLife getField1() {
      return field1;
    }
    
    public void setField1(EntityLife field1) {
      this.field1 = field1;
    }
    
  }
  
  
  @Test
  public void entityLifeEntityModel () {
    IEntityPlan<EntityLifeEntity> plan = planFactory.getEntityPlan(EntityLifeEntity.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    ModelFactory modelFactory = new ModelFactory();
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    EntityLifeEntity instance = new EntityLifeEntity(EntityLife.ACTIVE);
    model.setValue(instance);
    
    INodeModel[] items = model.getMembers();
    Assert.assertEquals(1,  items.length);
    for (int i = 0; i < items.length; i++) {
      Assert.assertTrue(items[i] instanceof IItemModel);
      IItemModel itemModel = (IItemModel)items[i];
      Assert.assertEquals(false, itemModel.getType().isPrimitive());
      Assert.assertEquals(false, itemModel.getType().isNullable());
      Assert.assertEquals(true, itemModel.getType() instanceof EntityLifeType);
    }
    Object[] values = new Object[1];
    for (int i = 0; i < values.length; i++) {
      IItemModel itemModel = (IItemModel)items[i];
      values[i] = itemModel.getValue();
    }
    Assert.assertEquals(EntityLife.ACTIVE, values[0]);
  }

  
}
