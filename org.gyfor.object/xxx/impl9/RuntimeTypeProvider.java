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
package org.gyfor.object.plan.impl9;

import java.lang.reflect.Method;

import org.gyfor.object.type.IType;
import org.gyfor.object.plan.IRuntimeTypeProvider;


public class RuntimeTypeProvider extends RuntimeProvider implements IRuntimeTypeProvider {

  private final IType<?> staticType;
  
  
  public RuntimeTypeProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.staticType = null;
  }

  
  public RuntimeTypeProvider (IType<?> staticType, String[] appliesTo) {
    super (appliesTo);
    if (staticType == null) {
      throw new IllegalArgumentException("Static type cannot be null");
    }
    this.staticType = staticType;
  }

  
  /**
   * Get the type for the designated fields. The designated fields are
   * those listed by the getAppliesTo method.
   */
  @Override
  public IType<?> getType(Object instance) {
    if (isRuntime()) {
      if (instance == null) {
        throw new IllegalArgumentException();
      }
      IType<?> x = invokeRuntime(instance);
      if (x == null) {
        throw new RuntimeException("Runtime type cannot be null");
      }
      return x;
    } else {
      return staticType;
    }
  }

}
