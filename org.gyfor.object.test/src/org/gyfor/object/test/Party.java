package org.gyfor.object.test;


import java.util.ArrayList;
import java.util.List;

import org.gyfor.object.Embeddable;


@Embeddable
public class Party {

  @SuppressWarnings("unused")
  private String name;

  private List<Location> locations;

  public Party(String name, String number, String streetName, String suburb) {
    this.name = name;
    this.locations = new ArrayList<>();
    this.locations.add(new Location(number, streetName, suburb));
  }

}
