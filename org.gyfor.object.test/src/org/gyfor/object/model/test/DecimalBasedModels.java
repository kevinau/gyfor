package org.gyfor.object.model.test;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.gyfor.math.Decimal;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.test.data.DecimalBased;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DecimalBasedModels {

  private IEntityModel model;
  private DecimalBased instance;
  

  private void getSetTest (String fieldName, Object value, String okSource, Supplier<?> supplier) {
    IItemModel itemModel = model.selectItemModel(fieldName);
    itemModel.setValue(value);
    Assert.assertEquals("Field " + fieldName, value, supplier.get());
    Assert.assertEquals("Field " + fieldName, value, itemModel.getValue());
    
    itemModel.setValueFromSource(okSource);
    Assert.assertEquals("Field " + fieldName, value, supplier.get());
    Assert.assertEquals("Field " + fieldName, value, itemModel.getValue());
  }
  
  
  @Before
  public void setup () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    model = modelFactory.buildEntityModel(DecimalBased.class);
    instance = new DecimalBased();
    model.setValue(instance);
  }
  
  
  @Test
  public void floatTest () {
    getSetTest("float1", (Float)123.45F, "123.45", () -> instance.float1);
  }
  
  @Test
  public void doubleTest () {
    getSetTest("double1", (Double)1234.56, "1234.56", () -> instance.double1);
  }
  
  @Test
  public void bidDecimalTest () {
    getSetTest("bigDecimal", new BigDecimal(1234.56), "1234.56", () -> instance.bigDecimal);
  }
  
  @Test
  public void decimalTest () {
    getSetTest("decimal", new Decimal(1234.56), "1234.56", () -> instance.decimal);
  }
  
}
