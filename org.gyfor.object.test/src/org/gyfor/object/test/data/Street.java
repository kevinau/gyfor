package org.gyfor.object.test.data;

import org.gyfor.object.Embeddable;


@Embeddable
public class Street {

  @SuppressWarnings("unused")
  private String number;

  @SuppressWarnings("unused")
  private String streetName;

  public Street() {
  }

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
