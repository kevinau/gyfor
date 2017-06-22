package org.gyfor.object.test.data;


import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.Embeddable;


@SuppressWarnings("unused")
@Embeddable
public class Party {

  private String name;

  private Location home;
  
  private List<Location> locations;

  public Party(String name, String number, String streetName, String suburb) {
    this.name = name;
    this.locations = new ArrayList<>();
    this.locations.add(new Location(number, streetName, suburb));
    this.home = new Location("19", streetName, suburb);
  }

}
