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
package org.gyfor.object.plan;

import org.gyfor.object.path2.IPathExpression;

public interface IRuntimeFactoryProvider extends IRuntimeProvider {

  /**
   * Get a list of XPaths expressions that identify the fields that this plan
   * applies to. All matching fields will use the same createNewValue method.
   * The list should never be empty, but there is no problem if it is. The
   * XPaths here are relative to the class that contains the
   * factory method.
   * 
   * @return list of XPath expressions
   */
  @Override
  public IPathExpression[] getAppliesTo();

  /**
   * Get a list of field names that the createNewValue method depends on. Some
   * implementations may compute this from the code of the createNewValue
   * method, others will specify it explicitly. The names here are relative to
   * the control which contains the IRuntimeFactoryProvider method.
   * 
   * @return list of field names
   */
  @Override
  public IPathExpression[] getDependsOn();

  /**
   * Create a new value for the designated fields. The designated fields are
   * those listed by the getAppliesTo method.
   * 
   * @return the default value for the designated fields.
   */
  public Object createNewValue(Object instance);

  /**
   * Does this provider return an initial value, or does it only return a 
   * value during runtime.  A provider that returns a value at runtime requires
   * an instance value.
   * 
   * @return
   */
  @Override
  public boolean isRuntime();

}
