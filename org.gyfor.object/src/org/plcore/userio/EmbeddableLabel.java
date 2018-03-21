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
package org.plcore.userio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EmbeddableLabel {

  /**
   * A shortish heading that describes the group of fields. This is only used if
   * the ebmedded field does not have an explicit heading. If not supplied, the
   * computed heading from the embedded field is used.
   */
  String heading() default "\u0000";

  /**
   * A description of the group of fields. This description adds to the
   * understanding of the group of fields. used if
   * the ebmedded field does not have an explicit heading.
   */
  String description() default "\u0000";

}
