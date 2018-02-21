/*-
 * Copyright (C) 2002, 2017, Oracle and/or its affiliates. All rights reserved.
 *
 * This file was distributed by Oracle as part of a version of Oracle Berkeley
 * DB Java Edition made available at:
 *
 * http://www.oracle.com/technetwork/database/database-technologies/berkeleydb/downloads/index.html
 *
 * Please see the LICENSE file included in the top-level directory of the
 * appropriate version of Oracle Berkeley DB Java Edition for a copy of the
 * license and additional information.
 */

package org.gyfor.berkeleydb.learn;

import org.gyfor.berkeleydb.DataStore;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;


//@Component(service = ExampleInventoryRead2.class, immediate = true)
public class ExampleInventoryRead2 {

  @Reference
  private DataStore dataStore;
  

  @Activate
  public void activate(ComponentContext context) {
    // Open the data accessor. This is used to retrieve
    // persistent objects.
    //DataAccessor da = new DataAccessor(dataStore.getEntityStore());

    // Get a cursor that will walk every
    // inventory object in the store.
    PrimaryIndex<String,Inventory> inventoryBySku = dataStore.getPrimaryIndex(String.class, Inventory.class);
    PrimaryIndex<String,Vendor> vendorByName = dataStore.getPrimaryIndex(String.class, Vendor.class);

    EntityCursor<Inventory> items = inventoryBySku.entities();

    try {
      System.out.println("ExampleInventoryRead2....... get all items");
      for (Inventory item : items) {
        displayInventoryRecord(vendorByName, item);
      }
      System.out.println("ExampleInventoryRead2....... done");
   } finally {
      items.close();
    }
  }

  private void displayInventoryRecord(PrimaryIndex<String,Vendor> vendorByName, Inventory theInventory) throws DatabaseException {

    System.out.println(theInventory.getSku() + ":");
    System.out.println("\t " + theInventory.getItemName());
    System.out.println("\t " + theInventory.getCategory());
    System.out.println("\t " + theInventory.getVendor());
    System.out.println("\t\tNumber in stock: " + theInventory.getVendorInventory());
    System.out.println("\t\tPrice per unit:  " + theInventory.getVendorPrice());
    System.out.println("\t\tContact: ");

    Vendor theVendor = vendorByName.get(theInventory.getVendor());
    assert theVendor != null;

    System.out.println("\t\t " + theVendor.getAddress());
    System.out.println("\t\t " + theVendor.getCity() + ", " + theVendor.getState() + " " + theVendor.getPostcode());
    System.out.println("\t\t Business Phone: " + theVendor.getBusinessPhoneNumber());
    System.out.println("\t\t Sales Rep: " + theVendor.getRepName());
    System.out.println("\t\t            " + theVendor.getRepPhoneNumber());
  }

}
