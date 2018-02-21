package org.gyfor.dao.berkeley;

import java.time.LocalDate;

import org.gyfor.value.VersionTime;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;


@Persistent(proxyFor = VersionTime.class)
class VersionTimeProxy implements PersistentProxy<VersionTime> {

  long seconds;
  int nanos;

  private VersionTimeProxy() {
  }


  @Override
  public void initializeProxy(VersionTime object) {
    seconds = object.getSeconds();
    nanos = object.getNanos();
  }


  @Override
  public VersionTime convertProxy() {
    return new VersionTime(seconds, nanos);
  }

}
