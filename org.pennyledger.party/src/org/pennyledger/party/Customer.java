package org.pennyledger.party;


@com.sleepycat.persist.model.Entity
public class Customer extends Party {

  private Address address;
  
  public Address getAddress() {
    return address;
  }
  
  public void setAddress(Address address) {
    this.address = address;
  }
  
}
