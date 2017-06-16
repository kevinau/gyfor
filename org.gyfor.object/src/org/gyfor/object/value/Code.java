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
package org.gyfor.object.value;


public class Code extends TextValue implements ICode {

  private static final long serialVersionUID = -4174110261045376592L;

  private final String description;
  private final boolean obsolete;
  private final boolean selfDescribing; 

  
	public Code (String code, String description, boolean obsolete) {
		super(code);
		this.description = description;
		this.obsolete = obsolete;
    this.selfDescribing = code.equals(description);
	}
  
  
  public Code (String code, String description) {
    this (code, description, false);
  }
  
  
  public Code (String code) {
    this (code, code, false);
  }
  
  
  @Override
  public String getCode () {
    return super.toString();
  }
  
  
  @Override
  public String getDescription () {
    return description;
  }
  
  
  @Override
  public boolean isSelfDescribing () {
    return selfDescribing;
  }
  
  
  @Override
  public String asPriorValue () {
    if (selfDescribing) {
      return super.toString();
    } else {
      return super.toString() + " " + description;
    }
  }


  @Override
  public boolean isObsolete() {
  	return obsolete;
  }

}
