package org.gyfor.object.test.data;

import org.gyfor.object.Embeddable;
import org.gyfor.object.EntryMode;
import org.gyfor.object.Mode;

@Embeddable
public class ModeTestEntity {

  String field0;
  
  @Mode(EntryMode.ENABLED)
  String field1;
  
  @Mode(EntryMode.DISABLED)
  String field2;
  
  @Mode(EntryMode.VIEW)
  String field3;
  
  @Mode(EntryMode.HIDDEN)
  String field4;
  
}
