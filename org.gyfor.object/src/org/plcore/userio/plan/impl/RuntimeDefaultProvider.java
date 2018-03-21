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

import org.plcore.userio.plan.IRuntimeDefaultProvider;


public class RuntimeDefaultProvider extends RuntimeProvider implements IRuntimeDefaultProvider {

  private final Object defaultValue;
  
  public RuntimeDefaultProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.defaultValue = null;
  }

  
  public RuntimeDefaultProvider (Class<?> klass, FieldDependency fieldDependency, Field field, String[] appliesTo) {
    super (klass, fieldDependency, field, appliesTo);
    this.defaultValue = null;
  }

  
  public RuntimeDefaultProvider (Object defaultValue, String[] appliesTo) {
    super (appliesTo);
    this.defaultValue = defaultValue;
  }

  
  public RuntimeDefaultProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
  }
  
  
  public RuntimeDefaultProvider (Class<?> klass, FieldDependency fieldDependency, Field field, String fieldName) {
    this (klass, fieldDependency, field, new String[] {fieldName});
  }
  
  
  /**
   * Get the default value for the designated fields. The designated fields are
   * those listed by the getAppliesTo method.
   * 
   * @return the default value for the designated fields.
   */
  @Override
  public Object getDefaultValue(Object instance) {
    if (isRuntime()) {
      if (instance == null) {
        throw new IllegalArgumentException();
      }
      return invokeRuntime(instance);
    } else {
      return defaultValue;
    }
  }


  @Override
  public String toString() {
    return "RuntimeDefaultProvider [defaultValue=" + defaultValue + ", " + super.toString() + "]";
  }

}
