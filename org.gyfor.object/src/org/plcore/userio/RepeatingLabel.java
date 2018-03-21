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
@Target(ElementType.FIELD)
public @interface RepeatingLabel {

  /**
   * The on screen label associated with this repeating field. If not supplied,
   * a default label is calculated from the field name using camel case
   * conventions to break the name into words. This can be specified as an empty
   * string, in which case the repeating field has no label/legend.
   */
  String value() default "\u0000";

  /**
   * A short description of the repeating field. This description adds to the
   * understanding of the group of fields that make up the repeating field.
   */
  String description() default "";

  /**
   * A class that implements IIndexLabel.  This class provides index labels.
   * If not supplied, a default class provides numerical indexes, starting at 1.
   */
  Class<? extends IIndexLabelProvider> indexLabels() default NumberedIndexLabelProvider.class;
  
}
