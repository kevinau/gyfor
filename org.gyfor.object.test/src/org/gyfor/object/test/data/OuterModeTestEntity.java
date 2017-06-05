package org.gyfor.object.test.data;

import org.gyfor.object.EntryMode;
import org.gyfor.object.Mode;

public class OuterModeTestEntity {

  @Mode(EntryMode.VIEW)
  public ModeTestEntity inner;
  
  public OuterModeTestEntity () {
    inner = new ModeTestEntity();
  }
  
}
