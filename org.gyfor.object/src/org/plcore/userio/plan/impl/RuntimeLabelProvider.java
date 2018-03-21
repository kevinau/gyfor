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

import org.plcore.userio.plan.IRuntimeLabelProvider;

@Deprecated
public class RuntimeLabelProvider extends RuntimeProvider implements IRuntimeLabelProvider {

  private final String staticLabel;

  
  public RuntimeLabelProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.staticLabel = null;
  }

  
  public RuntimeLabelProvider (String staticLabel, String[] appliesTo) {
    super (appliesTo);
    if (staticLabel == null) {
      throw new IllegalArgumentException("Static label cannot be null");
    }
    this.staticLabel = staticLabel;
  }

  
  /**
   * Get the default value for the designated fields. The designated fields are
   * those listed by the getAppliesTo method.
   * 
   * @return the default value for the designated fields.
   */
  @Override
  public String getLabel(Object instance) {
    if (isRuntime()) {
      if (instance == null) {
        throw new IllegalArgumentException();
      }
      String x = invokeRuntime(instance);
      if (x == null) {
        throw new RuntimeException("Runtime label cannot be null");
      }
      return x;
    } else {
      return staticLabel;
    }
  }

}
