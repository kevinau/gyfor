package org.gyfor.object.model.test;

import org.gyfor.object.DefaultFor;
import org.gyfor.object.IOField;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.junit.Assert;
import org.junit.Test;


public class EntityDefaults {

  private static class SimpleEntity0 {
    @IOField 
    String name = "Initial and default value";
  }
  
  
  private static class SimpleEntity1 {
    @IOField 
    String name = "Initial and default value";
    
    @DefaultFor("name")
    String nameDefault = "Default value";
  }
  
  
  private static class SimpleEntity2 {
    @IOField 
    String name;
    
    @DefaultFor("name")
    String nameDefault() {
      System.out.println("nameDefault calculated");
      return "Default value";
    }
  }
  
  
  private static class SimpleEntity3 {
    @IOField 
    String name;
    
    @IOField
    int nameNumber = 12;
    
    @DefaultFor("name")
    String nameDefault3() {
      System.out.println("nameDefault3 calculated");
      return "Name " + nameNumber;
    }
  }
  
  
  private static class SimpleEntity4 {
    @IOField 
    String name;
    
    @DefaultFor("name")
    private static String nameDefault4() {
      return "Default value";
    }
  }
  
  
  private static class SimpleEntity5 {
    @IOField 
    String name;
    
    @DefaultFor("name")
    private static String nameDefault4 = "Default value";
  }
  
  
  private static class SimpleEntity6 {
    @IOField 
    String name;
    
    @IOField 
    String field;
    
    @IOField
    int namePart1 = 12;
    
    @IOField
    int namePart2 = 34;
    
    @DefaultFor({"name", "field"})
    String nameDefault3() {
      return "Name " + namePart1 + "-" + namePart2;
    }
  }
  
  
  @Test
  public void classAssignedDefault () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity0.class);
    
    SimpleEntity0 instance = new SimpleEntity0();
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Initial and default value", nameItem.getValue());
    Assert.assertEquals("Initial and default value", nameItem.getDefaultValue());
  }
  
  
  @Test
  public void defaultField () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity1.class);
    
    SimpleEntity1 instance = new SimpleEntity1();
    instance.name = "Assigned value";
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Assigned value", nameItem.getValue());
    Assert.assertEquals("Default value", nameItem.getDefaultValue());
  }
  
  
  @Test
  public void defaultMethod () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity2.class);
    
    SimpleEntity2 instance = new SimpleEntity2();
    instance.name = "Assigned value";
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Assigned value", nameItem.getValue());
    Assert.assertEquals("Default value", nameItem.getDefaultValue());
  }
  
  
  @Test
  public void dependentDefaultMethod () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity3.class);
    
    SimpleEntity3 instance = new SimpleEntity3();
    instance.name = "Assigned name";
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Assigned name", nameItem.getValue());
    Assert.assertEquals("Name 12", nameItem.getDefaultValue());
    
    // Make name item the same as the default value
    nameItem.setValue("Name 12");
    Assert.assertEquals("Name 12", nameItem.getValue());

    // Change the dependent value, and both the item value and the default should both change
    Assert.assertEquals("Name 12", nameItem.getDefaultValue());
    IItemModel numberItem = model.selectItemModel("nameNumber");
    numberItem.setValue(34);
    System.out.println((String)nameItem.getDefaultValue());
    Assert.assertEquals("Name 34", nameItem.getValue());
    Assert.assertEquals("Name 34", nameItem.getDefaultValue());    
  }
  
  
  @Test
  public void dependentDefaultMethodWithError () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity3.class);
    
    SimpleEntity3 instance = new SimpleEntity3();
    instance.name = "Name 12";
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Name 12", nameItem.getValue());
    Assert.assertEquals("Name 12", nameItem.getDefaultValue());
    
    // Change the dependent value, and both the item value and the default should both change
    IItemModel numberItem = model.selectItemModel("nameNumber");
    numberItem.setValueFromSource("abcd");
    Assert.assertEquals("Name 12", nameItem.getValue());
    Assert.assertEquals("Name 12", nameItem.getDefaultValue());    
  }
  
  
  @Test
  public void staticDefaultField () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity4.class);
    
    SimpleEntity4 instance = new SimpleEntity4();
    instance.name = "Assigned value";
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Assigned value", nameItem.getValue());
    Assert.assertEquals("Default value", nameItem.getDefaultValue());
  }
  
  
  @Test
  public void staticDefaultMethod () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity5.class);
    
    SimpleEntity5 instance = new SimpleEntity5();
    instance.name = "Assigned value";
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Assigned value", nameItem.getValue());
    Assert.assertEquals("Default value", nameItem.getDefaultValue());
  }
  
  
  @Test
  public void multipleDependsAndAppliesTo () {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel model = modelFactory.buildEntityModel(SimpleEntity6.class);
    
    SimpleEntity6 instance = new SimpleEntity6();
    instance.name = "Assigned value";
    model.setValue(instance);

    IItemModel nameItem = model.selectItemModel("name");
    Assert.assertEquals("Assigned value", nameItem.getValue());
    Assert.assertEquals("Name 12-34", nameItem.getDefaultValue());
  }
  
  
}
