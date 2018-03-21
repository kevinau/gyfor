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

import org.plcore.userio.type.NumberSign;
import org.plcore.userio.type.TextCase;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface IOField {
  
  /**
   * The name of a IType component.
   */
  String type() default "";

  
  /**
   * An optional field length.  This can be used in place of a full type for String 
   * based fields.  This overrides any explicit type information found according to
   * the rules for type() described above.
   */
  int length() default -1;

  /**
   * An optional regex pattern. This can be used in place of a full type for
   * String based fields.
   */
  String pattern() default "";

  
  /**
   * Change the case of entered text.  This can be used in lieu of a pattern.  If a pattern
   * is specified, this does not need to be set.
   * <p>
   * Note.  This annotation is called "xcase" rather than the more obvious "case" because "case"
   * is a reserved word in Java. 
   */
  TextCase xcase() default TextCase.UNSPECIFIED;
  
  
  /**
   * Optional text that is displayed if the data entry field is blank, and the field is not Optional.  If
   * not specified, a required message is obtained from the IType class of the field.
   */
  String requiredMessage() default "";
  
  
  /**
   * Optional text that is displayed if data entry does not match the pattern.  This is only used if a pattern
   * is specified.  If data entry is incomplete, this message is displayed as an incomplete message.  If the
   * data entry does not match the pattern at all, this message is displayed as an error message.  If not 
   * specified, the error/incomplete message is derived from the pattern.
   */
  String errorMessage() default "";
  
  
  NumberSign sign() default NumberSign.UNSPECIFIED;
  
  
  /**
   * An optional precision. This can be used in place of a full type for numeric
   * fields. It specifies the number of digits in a number. This overrides any
   * explicit type information found according to the rules for type() described
   * above.
   */
  int precision() default -1;
  
  
  /**
   * An optional scale. This can be used in place of a full type for numeric
   * fields. It specifies the number of digits that are allowed to the right of
   * the decimal digit. This overrides any explicit type information found
   * according to the rules for type() described above.
   */
  int scale() default -1;
  
  long min() default Long.MIN_VALUE;
  
  long max() default Long.MAX_VALUE;
  
//  /**
//   * A field, method or value that returns whether the data entry is being
//   * compared against a prior value. This annotation is also available on data
//   * entry panels ({@see Panel}).
//   */
//  String compareMode() default "";
//
//  /**
//   * A field or method that returns a prior value for the data entry field. The
//   * field or return type must match the type of the data entry field. This
//   * annotation is also available on data entry panels ({@see Panel}).
//   */
//  String priorValue() default "";

//  boolean unique() default false;
  
}
