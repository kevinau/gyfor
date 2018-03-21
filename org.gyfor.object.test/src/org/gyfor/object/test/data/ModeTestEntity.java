package org.gyfor.object.test.data;

import org.plcore.userio.Embeddable;
import org.plcore.userio.EntryMode;
import org.plcore.userio.IOField;
import org.plcore.userio.Mode;


@Embeddable
public class ModeTestEntity {

  @IOField
  String field0;

  @IOField
  @Mode(EntryMode.ENABLED)
  String field1;

  @IOField
  @Mode(EntryMode.DISABLED)
  String field2;

  @IOField
  @Mode(EntryMode.VIEW)
  String field3;

  @IOField
  @Mode(EntryMode.HIDDEN)
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

  public void setField1(String field1) {
    this.field1 = field1;
  }

  public String getField2() {
    return field2;
  }

  public void setField2(String field2) {
    this.field2 = field2;
  }

  public String getField3() {
    return field3;
  }

  public void setField3(String field3) {
    this.field3 = field3;
  }

  public String getField4() {
    return field4;
  }

  public void setField4(String field4) {
    this.field4 = field4;
  }

}
