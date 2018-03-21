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

import org.plcore.userio.EntryMode;
import org.plcore.userio.plan.IRuntimeModeProvider;


public class RuntimeModeProvider extends RuntimeProvider implements IRuntimeModeProvider {

  private final EntryMode mode;
  
  
  public RuntimeModeProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.mode = null;
  }

  
  public RuntimeModeProvider (EntryMode mode, String[] appliesTo) {
    super (appliesTo);
    this.mode = mode;
  }

  
  public RuntimeModeProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
  }
  
  
  /**
   * Get the field use for the designated fields. The designated fields are
   * those listed by the getAppliesTo method.
   * 
   * @return the field use for the designated fields.
   */
  @Override
  public EntryMode getEntryMode(Object instance) {
    if (isRuntime()) {
      return invokeRuntime(instance);
    } else {
      return mode;
    }
  }

}
