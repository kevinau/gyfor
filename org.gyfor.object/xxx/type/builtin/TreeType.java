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

import org.gyfor.object.type.builtin.CodeType;
import org.gyfor.value.ICode;


public class TreeType extends CodeType<ICode> {

  public TreeType (Class<ICode> codeClass) {
    super(codeClass);
  }
  
  
  public TreeType (Class<ICode> codeClass, List<ICode> valueList) {
    super(codeClass, valueList);
  }
  
}
