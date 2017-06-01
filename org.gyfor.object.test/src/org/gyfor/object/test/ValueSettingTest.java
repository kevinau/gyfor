package org.gyfor.object.test;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.Embeddable;
import org.gyfor.object.model.impl2.IEntityModel;
import org.gyfor.object.model.impl2.IItemModel;
import org.gyfor.object.model.impl2.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.junit.Assert;
import org.junit.Test;


public class ValueSettingTest {

  @Embeddable
  public static class Street {
    private String number;
    @SuppressWarnings("unused")
    private String streetName;
    
    public Street (String number, String streetName) {
      this.number = number;
      this.streetName = streetName;
    }
  }
  
  @Embeddable
  public static class Location {
    private Street street;
    
    @SuppressWarnings("unused")
    private String suburb;
  
    public Location (String number, String streetName, String suburb) {
      this.street = new Street(number, streetName);
      this.suburb = suburb;
    }
  }
  
  public static class Party {
    @SuppressWarnings("unused")
    private String name;
    
    private List<Location> locations;
    
    public Party (String name, String number, String streetName, String suburb) {
      this.name = name;
      this.locations = new ArrayList<>();
      this.locations.add(new Location(number, streetName, suburb));
    }

  }
  
  @Test
  public void test() {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);
    
    Party party = new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth");
    entity.setValue(party);
    entity.dump();
  }

  
  @Test
  public void testItemValues() {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);
    
    Party party = new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth");
    entity.setValue(party);
    
    IItemModel nameModel = entity.selectItemModel("name");
    Assert.assertNotNull(nameModel);
    String nameValue = nameModel.getValue();
    Assert.assertEquals("Kevin Holloway", nameValue);

    IItemModel suburbModel = entity.selectItemModel("locations[0].suburb");
    Assert.assertNotNull(suburbModel);
    String suburbValue = suburbModel.getValue();
    Assert.assertEquals("Nailsworth", suburbValue);
    
    IItemModel numberModel = entity.selectItemModel("locations[0].street.number");
    Assert.assertNotNull(numberModel);
    String numberValue = numberModel.getValue();
    Assert.assertEquals("17", numberValue);
    
    numberModel.setValue("19");
    Assert.assertEquals("19", party.locations.get(0).street.number);
    
  }

}
