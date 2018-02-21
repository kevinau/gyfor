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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.gyfor.berkeley.DataStore;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;


@Component(service = ExampleDatabasePut2.class, immediate = true)
public class ExampleDatabasePut2 {

  @Reference
  private DataStore dataStore;

  private ComponentContext context;

  @Activate
  public void activate(ComponentContext context) {
    this.context = context;
    System.out.println("loading vendors db....");
    loadVendorsDb();

    System.out.println("loading inventory db....");
    loadInventoryDb();

    // Get a cursor that will walk every
    // inventory object in the store.
    PrimaryIndex<String, Inventory> inventoryBySku = dataStore.getPrimaryIndex(String.class, Inventory.class);
    PrimaryIndex<Integer, Vendor> vendorById = dataStore.getPrimaryIndex(Integer.class, Vendor.class);
    SecondaryIndex<String, Integer, Vendor> vendorByName = dataStore.getSecondaryIndex(vendorById, String.class, "vendor");

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

  private void loadVendorsDb() throws DatabaseException {

    // loadFile opens a flat-text file that contains our data
    // and loads it into a list for us to work with. The integer
    // parameter represents the number of fields expected in the
    // file.
    List<String[]> vendors = loadFile("vendors.txt", 8);

    PrimaryIndex<Integer, Vendor> vendorByName = dataStore.getPrimaryIndex(Integer.class, Vendor.class);

    // Now load the data into the store.
    for (int i = 0; i < vendors.size(); i++) {
      String[] sArray = (String[])vendors.get(i);
      Vendor theVendor = new Vendor();
      theVendor.setVendorName(sArray[0]);
      theVendor.setAddress(sArray[1]);
      theVendor.setCity(sArray[2]);
      theVendor.setState(sArray[3]);
      theVendor.setPostcode(sArray[4]);
      theVendor.setBusinessPhoneNumber(sArray[5]);
      theVendor.setRepName(sArray[6]);
      theVendor.setRepPhoneNumber(sArray[7]);

      // Put it in the store. Because we do not explicitly set
      // a transaction here, and because the store was opened
      // with transactional support, auto commit is used for each
      // write to the store.
      vendorByName.put(theVendor);
    }
  }

  private void loadInventoryDb() throws DatabaseException {

    // loadFile opens a flat-text file that contains our data
    // and loads it into a list for us to work with. The integer
    // parameter represents the number of fields expected in the
    // file.
    List<String[]> inventoryArray = loadFile("inventory.txt", 6);

    // Now load the data into the store. The item's sku is the
    // key, and the data is an Inventory class object.
    PrimaryIndex<String, Inventory> inventoryBySku = dataStore.getPrimaryIndex(String.class, Inventory.class);
    // SecondaryIndex<String,String,Inventory> inventoryByName =
    // dataStore.getSecondaryIndex(
    // inventoryBySku, String.class, "itemName");

    for (int i = 0; i < inventoryArray.size(); i++) {
      String[] sArray = (String[])inventoryArray.get(i);
      String sku = sArray[1];

      Inventory theInventory = new Inventory();
      theInventory.setItemName(sArray[0]);
      theInventory.setSku(sku);
      theInventory.setVendorPrice((new Float(sArray[2])).floatValue());
      theInventory.setVendorInventory((new Integer(sArray[3])).intValue());
      theInventory.setCategory(sArray[4]);
      theInventory.setVendor(sArray[5]);

      // Put it in the store. Note that this causes our secondary key
      // to be automatically updated for us.
      inventoryBySku.put(theInventory);
    }
  }

  private List<String[]> loadFile(String fileName, int numFields) {
    URL url = context.getUsingBundle().getResource(fileName);
    List<String[]> records = new ArrayList<String[]>();
    try {
      String theLine = null;
      InputStream fis = url.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      while ((theLine = br.readLine()) != null) {
        String[] theLineArray = theLine.split("#");
        if (theLineArray.length != numFields) {
          System.out.println("Malformed line found in " + fileName);
          System.out.println("Line was: '" + theLine);
          System.out.println("length found was: " + theLineArray.length);
          System.exit(-1);
        }
        records.add(theLineArray);
      }
      // Close the input stream handle
      fis.close();
    } catch (FileNotFoundException ex) {
      System.err.println(fileName + " does not exist.");
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    return records;
  }

  
  private void displayInventoryRecord(SecondaryIndex<String, Integer, Vendor> vendorByName, Inventory theInventory) throws DatabaseException {

    System.out.println(theInventory.getSku() + ":");
    System.out.println("\t " + theInventory.getItemName());
    System.out.println("\t " + theInventory.getCategory());
    System.out.println("\t " + theInventory.getVendor());
    System.out.println("\t\tNumber in stock: " + theInventory.getVendorInventory());
    System.out.println("\t\tPrice per unit:  " + theInventory.getVendorPrice());
    System.out.println("\t\tContact: ");

    Vendor theVendor = vendorByName.get(theInventory.getVendor());
    assert theVendor != null;

    System.out.println("\t\t " + theVendor.getId());
    System.out.println("\t\t " + theVendor.getVendorName());
    System.out.println("\t\t " + theVendor.getAddress());
    System.out.println("\t\t " + theVendor.getCity() + ", " + theVendor.getState() + " " + theVendor.getPostcode());
    System.out.println("\t\t Business Phone: " + theVendor.getBusinessPhoneNumber());
    System.out.println("\t\t Sales Rep: " + theVendor.getRepName());
    System.out.println("\t\t            " + theVendor.getRepPhoneNumber());
  }

}
