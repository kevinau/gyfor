package org.gyfor.object.test.data;

import org.gyfor.object.EntryMode;
import org.gyfor.object.Mode;


public class OuterModeTestEntity {

  public ModeTestEntity inner;

  public OuterModeTestEntity() {
    inner = new ModeTestEntity();
  }

  public ModeTestEntity getInner() {
    return inner;
  }

  @Mode(EntryMode.VIEW)
  public void setInner(ModeTestEntity inner) {
    this.inner = inner;
  }

}
