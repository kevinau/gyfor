package org.plcore.http;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Resources.class)
public @interface Resource {

  /**
   * Path extension.  This path is added to the web page @Context to form the path prefix.  If no path extension is specified,
   * a list of extensions should be specified.
   */
  String path() default "";
  
  /**
   * Extensions that are treated as a resource.  If empty, all files within the base are treated as resources.
   */
  String[] extensions() default {};
  
  /**
   * Location of resources within the Bundle.
   */
  String location() default "";

  /**
   * Is the location dynamically calculated using IDynamicResourceLocation
   */
  boolean dynamic() default false;

}
