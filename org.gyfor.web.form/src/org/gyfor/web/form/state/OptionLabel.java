package org.gyfor.web.form.state;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OptionLabel {
  
  /**
   * The on screen label associated with this option. If not supplied,
   * a default label is calculated from the enumeration name using sentence case,
   * with underscores separating the words.
   */
  String label() default "\u0000";

  /**
   * The name of a image file.  If supplied, the screen label is replaced with
   * the icon image.
   */
  String icon() default "";
  
  /** 
   * A short description of the data entry item.  This description adds to the
   * understanding of the data entry item.
   */
  String description() default "";
  

}
