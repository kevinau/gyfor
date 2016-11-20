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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.gyfor.object.plan.IRuntimeOccursProvider;


public class RuntimeOccursProvider extends RuntimeProvider implements IRuntimeOccursProvider {

  private final Method method;
  private final Field field;
  private final int size;
  private final String[] dependsOn;
  
  
  public RuntimeOccursProvider (int size, String[] appliesTo) {
    super (appliesTo);
    this.method = null;
    this.field = null;
    this.size = size;
    this.dependsOn = new String[0];
  }

  
  public RuntimeOccursProvider (Field field, String[] appliesTo) {
    super (appliesTo);
    this.method = null;
    this.field = field;
    this.size = 0;
    this.dependsOn = new String[] {field.getName()};
  }

  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (appliesTo);
    this.method = method;
    this.field = null;
    this.size = 0;
    
    // Calculate dependencies
    List<String> dx = fieldDependency.getDependencies(klass.getName(), method.getName());
    this.dependsOn = dx.toArray(new String[dx.size()]);
  }

  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
  }
  
  
  /**
   * Get a list of field names that the getFieldUse method depends on. Some
   * implementations may compute this from the code of the getFieldUse method,
   * others will specify it explicitly.  The names here are relative to the control
   * which contains the IRuntimeUseProvider.
   * 
   * @return list of field names
   */
  @Override
  public String[] getDependsOn() {
    return dependsOn;
  }

  
  /**
   * Get the array size for the designated array fields. The designated fields are
   * those listed by the getAppliesTo method.
   */
  @Override
  public int getOccurs(Object instance) {
    if (field != null) {
      try {
        field.setAccessible(true);
        int n = (Integer)field.get(instance);
        return n;
      } catch (SecurityException ex) {
        throw new RuntimeException(ex);
      } catch (IllegalArgumentException ex) {
        throw new RuntimeException(ex);
      } catch (IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
    } else if (method != null) {
      try {
        method.setAccessible(true);
        int n = (Integer)method.invoke(instance);
        return n;
      } catch (SecurityException ex) {
        throw new RuntimeException(ex);
      } catch (IllegalArgumentException ex) {
        throw new RuntimeException(ex);
      } catch (IllegalAccessException ex) {
        throw new RuntimeException(ex);
      } catch (InvocationTargetException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      return size;
    }
  }
  
  
  @Override 
  public String toString () {
    StringBuilder s = new StringBuilder();
    s.append("RuntimeOccursProvider(");
    s.append(size);
    s.append(',');
    if (field != null) {
      s.append(field.getName());
    }
    s.append(',');
    if (method != null) {
      s.append(method.getName());
    }
    s.append(",[");
    String[] appliesTo = getAppliesTo();
    for (int i = 0; i < appliesTo.length; i++) {
      if (i > 0) s.append(",");
      s.append(appliesTo[i].toString());
    }
    s.append("],[");
    for (int i = 0; i < dependsOn.length; i++) {
      if (i > 0) s.append(",");
      s.append(dependsOn[i]);
    }
    s.append("])");
    return s.toString();
  }


  @Override
  public boolean isRuntime() {
    return (method != null || field != null) && dependsOn.length > 0;
  }

}
