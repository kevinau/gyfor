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

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;


@Entity
public class Vendor {

  private String repName;
  private String address;
  private String city;
  private String state;
  private String postcode;
  private String bizPhoneNumber;
  private String repPhoneNumber;

  // Primary key is the vendor's name
  // This assumes that the vendor's name is
  // unique in the database.
  @PrimaryKey(sequence = "Vendor_seq")
  private int id;

  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String vendor;

  public void setRepName(String data) {
    repName = data;
  }

  public void setAddress(String data) {
    address = data;
  }

  public void setCity(String data) {
    city = data;
  }

  public void setState(String data) {
    state = data;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public void setBusinessPhoneNumber(String data) {
    bizPhoneNumber = data;
  }

  public void setRepPhoneNumber(String data) {
    repPhoneNumber = data;
  }

  public void setVendorName(String data) {
    vendor = data;
  }

  public int getId() {
    return id;
  }

  public String getRepName() {
    return repName;
  }

  public String getAddress() {
    return address;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getPostcode() {
    return postcode;
  }

  public String getBusinessPhoneNumber() {
    return bizPhoneNumber;
  }

  public String getRepPhoneNumber() {
    return repPhoneNumber;
  }

  public String getVendorName() {
    return vendor;
  }

}
