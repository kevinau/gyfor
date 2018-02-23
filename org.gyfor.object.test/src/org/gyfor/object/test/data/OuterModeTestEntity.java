package org.gyfor.object.test.data;

import org.gyfor.object.EntryMode;
import org.gyfor.object.IOField;
import org.gyfor.object.Mode;


public class OuterModeTestEntity {

  @IOField
  @Mode(EntryMode.VIEW)
  public ModeTestEntity inner;

  public OuterModeTestEntity() {
    inner = new ModeTestEntity();
  }

  public ModeTestEntity getInner() {
    return inner;
  }

  public void setInner(ModeTestEntity inner) {
    this.inner = inner;
  }

}
