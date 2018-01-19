package org.gyfor.object.model.test;

import java.text.ParseException;
import java.util.function.Supplier;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.test.data.CodeBased;
import org.gyfor.object.test.data.CodeBased.Gender;
import org.gyfor.object.value.Code;
import org.gyfor.object.value.EntityLife;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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
    getSetTest("code", new Code("wed"), "wed", () -> instance.code);
  }
  
}
