package org.gyfor.berkeleydb;

import org.gyfor.math.Decimal;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;


@Persistent(proxyFor = Decimal.class)
class DecimalProxy implements PersistentProxy<Decimal> {

  String stringValue;

  private DecimalProxy() {
  }


  @Override
  public void initializeProxy(Decimal object) {
    stringValue = object.toString();
  }


  @Override
  public Decimal convertProxy() {
    return new Decimal(stringValue);
  }

}
