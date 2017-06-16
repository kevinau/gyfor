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


import java.util.List;

import org.gyfor.object.type.builtin.CodeBasedType;
import org.gyfor.object.value.ICode;


public class TreeType extends CodeBasedType<ICode> {

  public TreeType () {
    this (null, null);
  }
  
  
  public TreeType (List<ICode> valueList) {
    this (valueList, null, null);
  }
  
  
  public TreeType (String shortName, String longName) {
    this (null, shortName, longName);
  }
  
  
  public TreeType (List<ICode> valueList, String shortName, String longName) {
    super (valueList, shortName, longName);
  }
  
}
