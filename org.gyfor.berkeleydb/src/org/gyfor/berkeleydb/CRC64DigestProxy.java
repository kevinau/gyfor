package org.gyfor.berkeleydb;

import org.gyfor.util.CRC64Digest;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;


@Persistent(proxyFor = CRC64Digest.class)
class CRC64DigestProxy implements PersistentProxy<CRC64Digest> {

  long value;

  private CRC64DigestProxy() {
  }


  @Override
  public void initializeProxy(CRC64Digest object) {
    value = object.getLong();
  }


  @Override
  public CRC64Digest convertProxy() {
    return new CRC64Digest(value);
  }

}
