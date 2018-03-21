package org.plcore.userio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Mode {
  /**
   * The initial data entry "mode" for the data
   * entry field. The "mode" is one of:
   * <ul>
   * <li>Hidden. The data entry field is not shown, and occupies no space on
   * the form.</li>
   * <li>View only. No changes are allowed.</li>
   * <li>Edit. A normal data entry field.</li>
   * </ul>
   * The entry mode of a field can be overridden by a entry mode method that
   * returns the entry mode for a collection of named fields.  If an entry mode is UNSPECIFIED 
   * it inherits its value from the parent object.
   */
  EntryMode value() default EntryMode.UNSPECIFIED;

}
