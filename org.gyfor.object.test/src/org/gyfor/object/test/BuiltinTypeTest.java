package org.gyfor.object.test;

import java.util.List;

import org.gyfor.object.Entity;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.impl.EntityModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.object.plan.impl.PlanContext;
import org.junit.Assert;
import org.junit.Test;


public class BuiltinTypeTest {

  @SuppressWarnings("unused")
  @Entity
  public static class PrimitivesEntity {
    private boolean field1;
    private char field2;
    private byte field3;
    private short field4;
    private int field5;
    private long field6;
    private float field7;
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
  
  
  private IPlanContext planContext = new PlanContext();
  
  
  @Test
  public void primitivesEntityModel () {
    IEntityPlan<PrimitivesEntity> plan = planContext.getEntityPlan(PrimitivesEntity.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    IEntityModel model = new EntityModel(plan);
    
    PrimitivesEntity instance = new PrimitivesEntity(true, 'A', (byte)123, (short)1234, 12345, 123456L, 12.34F, 1234.5678);
    model.setValue(instance);
    
    List<INodeModel> items = model.getMembers();
    Assert.assertEquals(8,  items.size());
    for (int i = 0; i < 8; i++) {
      Assert.assertEquals(true, items.get(i).getType().isPrimitive());
      Assert.assertEquals(false, items.get(i).getType().isNullable());
    }
    Object[] values = new Object[8];
    for (int i = 0; i < 8; i++) {
      values[i] = items.get(i).getValue();
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
  
}