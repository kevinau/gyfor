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
package org.gyfor.object.plan.impl;

import java.lang.reflect.Method;

import org.gyfor.object.INode;
import org.gyfor.object.plan.IRuntimeDefaultProvider;


public class RuntimeDefaultProvider<T extends INode> extends RuntimeProvider<T> implements IRuntimeDefaultProvider<T> {

  private final Object defaultValue;
  
//  private Object instance;
  
  public RuntimeDefaultProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.defaultValue = null;
  }

  
  public RuntimeDefaultProvider (Object defaultValue, String[] appliesTo) {
    super (appliesTo);
    this.defaultValue = defaultValue;
  }

  
  public RuntimeDefaultProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
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
