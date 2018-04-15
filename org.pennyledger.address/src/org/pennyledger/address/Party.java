package org.pennyledger.address;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.userio.Entity;

@Component(property = "name=party")
@Entity
public class Party {

  private String name;
  
  @Reference
  private IAddress address;

  
  public String getName() {
    return name;
  }

  
  public IAddress getAddress() {
    return address;
  }

  
}
