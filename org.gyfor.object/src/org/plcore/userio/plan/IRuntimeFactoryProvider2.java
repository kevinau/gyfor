/*******************************************************************************
 * Copyright (c) 2008 Kevin Holloway (kholloway@geckosoftware.com.au).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 ******************************************************************************/
package org.plcore.userio.plan;


public interface IRuntimeFactoryProvider2 {

  /**
   * Get a list of XPaths expressions that identify the fields that this plan
   * applies to. All matching fields will use the same createNewValue method.
   * The list should never be empty, but there is no problem if it is. The
   * XPaths here are relative to the class that contains the
   * variant method.
   * 
   * @return list of XPath expressions
   */
  public String[] getAppliesTo();

  /**
   * Create a new value for the designated fields. The designated fields are
   * those listed by the getAppliesTo method.
   * 
   * @return the default value for the designated fields.
   */
  public Object createNewValue(Object instance);

}
