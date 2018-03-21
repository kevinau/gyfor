package org.gyfor.object.model.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.function.Supplier;

import org.gyfor.object.test.data.DateBased;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.PlanFactory;


public class DateBasedModels {

  private IEntityModel model;
  private DateBased instance;
  

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
    model = modelFactory.buildEntityModel(DateBased.class);
    instance = new DateBased();
    model.setValue(instance);
  }
  
  
  @Test
  public void dateTest () throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    getSetTest("date", dateFormat.parse("2011-05-31"), "2011-05-31", () -> instance.date);
  }
  
  @Test
  public void sqlDateTest () throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    getSetTest("sqlDate", new java.sql.Date(dateFormat.parse("2012-05-31").getTime()), "2012-05-31", () -> instance.sqlDate);
  }
  
  @Test
  public void localDateTest () {
    getSetTest("localDate", LocalDate.of(2013, 5, 31), "2013-05-31", () -> instance.localDate);
  }
  
}
