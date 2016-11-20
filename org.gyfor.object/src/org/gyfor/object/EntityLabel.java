/*******************************************************************************
 * Copyright  2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 *
 * Licensed under the EUPL, Version 1.1 only (the "Licence").  You may not use
 * this work except in compliance with the Licence.  You may obtain a copy of
 * the Licence at: http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the Licence for the
 * specific language governing permissions and limitations under the Licence.
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityLabel {

  /**
   * A shortish title that describes the entity. If not supplied it is
   * calculated from the class name using camel case conventions. This is hint
   * adds to the label.
   */
  String title() default "";


  /**
   * A short title that briefly describes the entity. If not supplied, a default
   * short title is calculated from the class name using camel case conventions
   * to break the name into words.
   */
  String shortTitle() default "";


  /**
   * A description of the entity. This description adds to the understanding of
   * the entity.
   */
  String description() default "";

}