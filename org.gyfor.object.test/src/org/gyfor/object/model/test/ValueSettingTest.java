package org.gyfor.object.model.test;

import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.Embeddable;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.ModelFactory;
import org.gyfor.object.plan.PlanFactory;
import org.junit.Assert;
import org.junit.Test;


public class ValueSettingTest {

  @Embeddable
  public static class Street {

    private String number;
    @SuppressWarnings("unused")
    private String streetName;

    public Street(String number, String streetName) {
      this.number = number;
      this.streetName = streetName;
    }

    public String getNumber() {
      return number;
    }

    public void setNumber(String number) {
      this.number = number;
    }

    public String getStreetName() {
      return streetName;
    }

    public void setStreetName(String streetName) {
      this.streetName = streetName;
    }

  }

  @Embeddable
  public static class Location {

    private Street street;

    @SuppressWarnings("unused")
    private String suburb;

    public Location(String number, String streetName, String suburb) {
      this.street = new Street(number, streetName);
      this.suburb = suburb;
    }

    public Street getStreet() {
      return street;
    }

    public void setStreet(Street street) {
      this.street = street;
    }

    public String getSuburb() {
      return suburb;
    }

    public void setSuburb(String suburb) {
      this.suburb = suburb;
    }

  }

  public static class Party {

    @SuppressWarnings("unused")
    private String name;

    private List<Location> locations;

    public Party(String name, String number, String streetName, String suburb) {
      this.name = name;
      this.locations = new ArrayList<>();
      this.locations.add(new Location(number, streetName, suburb));
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Location> getLocations() {
      return locations;
    }

    public void setLocations(List<Location> locations) {
      this.locations = locations;
    }

  }

  @Test
  public void test() {
    ModelFactory modelFactory = new ModelFactory(new PlanFactory());
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);

    Party party = new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth");
    entity.setValue(party);
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
