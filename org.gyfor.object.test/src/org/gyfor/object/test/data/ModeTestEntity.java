package org.gyfor.object.test.data;

import org.gyfor.object.Embeddable;
import org.gyfor.object.EntryMode;
import org.gyfor.object.Mode;


@Embeddable
public class ModeTestEntity {

  String field0;

  String field1;

  String field2;

  String field3;

  String field4;

  public String getField0() {
    return field0;
  }

  public void setField0(String field0) {
    this.field0 = field0;
  }

  public String getField1() {
    return field1;
  }

  @Mode(EntryMode.ENABLED)
  public void setField1(String field1) {
    this.field1 = field1;
  }

  public String getField2() {
    return field2;
  }

  @Mode(EntryMode.DISABLED)
  public void setField2(String field2) {
    this.field2 = field2;
  }

  public String getField3() {
    return field3;
  }

  @Mode(EntryMode.VIEW)
  public void setField3(String field3) {
    this.field3 = field3;
  }

  public String getField4() {
    return field4;
  }

  @Mode(EntryMode.HIDDEN)
  public void setField4(String field4) {
    this.field4 = field4;
  }

}
