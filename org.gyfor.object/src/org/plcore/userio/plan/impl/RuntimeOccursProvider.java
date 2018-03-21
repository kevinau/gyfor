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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.plcore.userio.plan.IRuntimeOccursProvider;


public class RuntimeOccursProvider extends RuntimeProvider implements IRuntimeOccursProvider {

  private final int size;
  
  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.size = 0;
  }

  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Field field, String[] appliesTo) {
    super (klass, fieldDependency, field, appliesTo);
    this.size = 0;
  }

  
  public RuntimeOccursProvider (int size, String[] appliesTo) {
    super (appliesTo);
    this.size = size;
  }

  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
  }
  
  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Field field, String fieldName) {
    this (klass, fieldDependency, field, new String[] {fieldName});
  }
  
  
  /**
   * Get the array size for the designated array fields. The designated fields are
   * those listed by the getAppliesTo method.
   */
  @Override
  public int getOccurs(Object instance) {
    if (isRuntime()) {
      if (instance == null) {
        throw new IllegalArgumentException();
      }
      return invokeRuntime(instance);
    } else {
      return size;
    }
  }
  
  
  @Override 
  public String toString () {
    return "RuntimeOccursProvider [size=" + size + ", " + super.toString() + "]";
  }

}
