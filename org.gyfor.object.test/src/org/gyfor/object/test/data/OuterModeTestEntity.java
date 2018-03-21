package org.gyfor.object.test.data;

import org.plcore.userio.EntryMode;
import org.plcore.userio.IOField;
import org.plcore.userio.Mode;


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
