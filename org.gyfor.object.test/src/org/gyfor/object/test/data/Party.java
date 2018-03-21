package org.gyfor.object.test.data;

import java.util.ArrayList;
import java.util.List;

import org.plcore.userio.Embeddable;
import org.plcore.userio.IOField;


@Embeddable
public class Party {

  @IOField
  private String name;

  @IOField
  private Location home;

  @IOField
  private List<Location> locations;

  public Party() {
    this.home = new Location();
  }

  public Party(String name, String number, String streetName, String suburb) {
    this.name = name;
    this.locations = new ArrayList<>();
    this.locations.add(new Location(number, streetName, suburb));
    this.home = new Location("19", streetName, suburb);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Location getHome() {
    return home;
  }

  public void setHome(Location home) {
    this.home = home;
  }

  public List<Location> getLocations() {
    return locations;
  }

  public void setLocations(List<Location> locations) {
    this.locations = locations;
  }

}
