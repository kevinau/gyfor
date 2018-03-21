/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.object.type.builtin;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.StringBasedType;
import org.gyfor.object.value.PasswordValue;


public class PasswordType extends StringBasedType<PasswordValue> {

  private static final String REQUIRED_MESSAGE = "password required";
  
  private static final int defaultMaxSize = 20;
  
  private String seed = "";
  
  
  public PasswordType () {
    this (defaultMaxSize);
  }

  
  public PasswordType (int maxSize) {
    super (maxSize);
  }

    
  public PasswordType (int maxSize, String seed) {
    super (maxSize);
    this.seed = seed;
  }

  
  public void setSeed (String seed) {
    this.seed = seed;
  }
  
    
  @Override
  public PasswordValue createFromString (String source) throws UserEntryException {
    PasswordValue pv = new PasswordValue(seed, source);
    validate (pv);
    return pv;
  }


  @Override
  public void validate(PasswordValue value, boolean nullable) throws UserEntryException {
  }
  
  
  @Override
  public String toEntryString (PasswordValue value, PasswordValue fillValue) {
    return "";
  }
  
  
  /**
   * Return the description of a value. Can return <code>null</code> if it does
   * not have a description that is distinct from its entry representation. If a
   * non-null value is returned, it should not duplicate the entry
   * representation.
   */
  @Override
  public String toDescriptionString (PasswordValue value) {
    return "...";
  }
  

  @Override
  public PasswordValue primalValue() {
    return new PasswordValue(seed, "");
  }


  @Override
  public PasswordValue newInstance(String source) {
    return new PasswordValue(seed, source);
  }


  @Override
  protected void validate(PasswordValue value) throws UserEntryException {
    // No further validation required
  }
  
  
  @Override
  public String getRequiredMessage () {
    return REQUIRED_MESSAGE;
  }
  
}
