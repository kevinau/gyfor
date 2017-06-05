package org.gyfor.object.test.data;

import org.gyfor.object.Embeddable;


@Embeddable
public class Location {

  @SuppressWarnings("unused")
  private Street street;

  @SuppressWarnings("unused")
  private String suburb;

  public Location(String number, String streetName, String suburb) {
    this.street = new Street(number, streetName);
    this.suburb = suburb;
  }
}

