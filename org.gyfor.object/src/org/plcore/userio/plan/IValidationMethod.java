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

import org.plcore.userio.UserEntryException;


public interface IValidationMethod extends Comparable<IValidationMethod> {

  /**
   * Get a list of field names that the getMode method depends on. Some
   * implementations may compute this from the code of the validate method,
   * others will specify it explicitly.  
   *
   * @return list of field names
   */
  public String[] getDependsOn ();
  
  
  /**
   * Validates the dependent fields. This method will be called 
   * whenever any of the dependent fields change their value.
   * 
   * @throws a DataEntryException if the validation fails
   */
  public void validate (Object instance) throws UserEntryException;
  
  
  public boolean isSlow ();
  
  
  /**
   * Returns the "order" of validation.  Zero is the reserved for pre-conversion validation.
   * After this, validation will be done in order according to the "order" of validation, with 
   * the lowest first.
   */
  public int getOrder ();
  
  public String getMethodName ();
  
}
