package org.gyfor.berkeleydb;

import java.time.LocalDate;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;


@Persistent(proxyFor = LocalDate.class)
class LocalDateProxy implements PersistentProxy<LocalDate> {

  long epochDay;

  private LocalDateProxy() {
  }


  @Override
  public void initializeProxy(LocalDate object) {
    epochDay = object.toEpochDay();
  }


  @Override
  public LocalDate convertProxy() {
    return LocalDate.ofEpochDay(epochDay);
  }

}
