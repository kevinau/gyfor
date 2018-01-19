package org.gyfor.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * Whether the data entry field can be empty. The Optional annotation will override any optional
 * characteristics of the field's type.
 * <p>
 * If @Optional is added to a set method, eg Optional or Optional(true), an empty
 * data entry field is accepted and assigned a <code>null</code> value, without invoking any 
 * field validation.
 * <p>
 * If 
 * @Optional is added to a set method with a false value, eg Optional(false), an empty data entry field will be 
 * fully validated and the value assigned will be that returned by the type's ... method.
 * <p>
 * Some examples will make this clear:
 * <table>
 * <tr>
 * <th>Annotation</th>
 * <th>Optional</th>
 * <th>Optional(false)</th>
 * </tr>
 * <tr>
 * <td>Date</td>
 * <td>Empty data entry is allowed.  If empty, the resultant field value is <code>null</code></td>
 * <td>Empty data entry is not allowed.  The resultant field value is always a valid Date object</td>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>Empty data entry is allowed.  If empty, the resultant field value is <code>null</code></td>
 * <td>Empty data entry is only allowed if a zero length field is allowed.  If data entry
 * is empty, the field value is the zero length string "" (it is not <code>null</code>)</td>
 * </tr>
 * <tr>
 * <td>Integer</td>
 * <td>Empty data entry is allowed.  If empty, the resultant field value is <code>null</code></td>
 * <td>Empty data entry is not allowed.  The field value is always a valid Integer object</td>
 * </tr>
 * <tr>
 * <td>primitive type</td>
 * <td><i>Not allowed.  A primitive value can never be <code>null</code></i></td>
 * <td>Empty data entry is not allowed.  The field value is a primitive value</td>
 * </tr>
 * </table>
 */
public @interface Optional {
  
  boolean value() default true;

}
