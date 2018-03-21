package org.gyfor.object.model.test;

import java.text.ParseException;
import java.util.function.Supplier;

import org.gyfor.object.test.data.CodeBased;
import org.gyfor.object.test.data.CodeBased.Gender;
import org.gyfor.object.test.data.CodeBased.Weekday;
import org.gyfor.value.EntityLife;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.PlanFactory;


public class CodeBasedModels {

  private IEntityModel model;
  private CodeBased instance;
  

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
    model = modelFactory.buildEntityModel(CodeBased.class);
    instance = new CodeBased();
    model.setValue(instance);
  }
  
  
  @Test
  public void booleanTest () throws ParseException {
    getSetTest("boolean1", Boolean.TRUE, "Y", () -> instance.boolean1);
  }
  
  
  @Test
  public void genderTest () throws ParseException {
    getSetTest("gender", Gender.FEMALE, "F", () -> instance.gender);
  }
  
  
  @Test
  public void entityLifeTest () throws ParseException {
    getSetTest("entityLife", EntityLife.RETIRED, "R", () -> instance.entityLife);
  }
  
  
  @Test
  public void codeTest () throws ParseException {
    getSetTest("weekday", Weekday.WED, "wed", () -> instance.weekday);
  }
  
}
