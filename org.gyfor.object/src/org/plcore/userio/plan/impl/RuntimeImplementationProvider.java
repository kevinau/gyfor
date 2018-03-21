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
package org.plcore.userio.plan.impl;

import java.lang.reflect.Method;

import org.plcore.userio.plan.IRuntimeImplementationProvider;


public class RuntimeImplementationProvider extends RuntimeProvider implements IRuntimeImplementationProvider {

  private final Class<?> implClass;
  
  
  public RuntimeImplementationProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.implClass = null;
  }

  
  public RuntimeImplementationProvider (Class<?> implClass, String[] appliesTo) {
    super (appliesTo);
    this.implClass = implClass;
  }

  
  public RuntimeImplementationProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
  }
  
  
  /**
   * Get the implementation for the designated fields. The designated fields are
   * those listed by the getAppliesTo method and must reference an interface.
   */
  @Override
  public Class<?> getImplementationClass(Object instance) {
    if (isRuntime()) {
      return invokeRuntime(instance);
    } else {
      return implClass;
    }
  }

}
