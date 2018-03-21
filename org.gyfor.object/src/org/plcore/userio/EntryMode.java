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

/**
 * Whether a data entry field is open for data entry, view only, or
 * "not applicable".
 * <p>
 * Implementation note. The order of the enumeration values matters. The
 * enumeration values go from the most liberal to the least liberal.
 * 
 */
public enum EntryMode {

  /**
   * No entry mode has been specified. The data entry mode will be inherited
   * from the parent object of this field. If there is no parent object, the
   * data entry mode will be ENABLED.
   */
  UNSPECIFIED,

  /**
   * Normal data entry. The user may view and enter a field value.
   */
  ENABLED,

  /**
   * Normal data entry, but disabled.  The user may view the field value, but not change it.
   */
  DISABLED,

  /**
   * Normal field display. The user may view the field value but cannot change
   * the value.  If a field can sometimes be ENABLED, use DISABLED when the user cannot change the field value.
   * If a field is never ENABLED, use VIEW when the user may view he field value but not change it.
   */
  VIEW,

  /**
   * The field is "not applicable". Typically this is used when a data entry
   * field is conditional on some other logic-- it can be open for data entry or
   * display under some conditions, and hidden from view under other conditions.
   * If a data field is "not applicable", the field labels are not shown.
   */
  HIDDEN;

}
