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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.gyfor.util.CamelCase;
import org.plcore.userio.UserEntryException;
import org.plcore.userio.plan.IFieldDependency;
import org.plcore.userio.plan.IMethodRunnable;
import org.plcore.userio.plan.IValidationMethod;


public class ValidationMethod implements IValidationMethod {

  private final Method method;
  private final IMethodRunnable runnable;
  private final String[] dependsOn;
  private final boolean isSlow;
  private final int subOrder;
  
  
  public ValidationMethod (Class<?> klass, IFieldDependency fieldDependency, Method method, boolean isSlow) {
    this.method = method;
    this.method.setAccessible(true);
    this.runnable = null;
    this.isSlow = isSlow;
    this.subOrder = 0;
    
    /* Calculate dependencies */
    List<String> dx = fieldDependency.getDependencies(klass.getName(), method.getName());
    this.dependsOn = dx.toArray(new String[dx.size()]);
  }


  public ValidationMethod (String[] dependsOn, IMethodRunnable runnable, int subOrder) {
    this.method = null;
    this.runnable = runnable;
    this.isSlow = true;
    this.subOrder = subOrder;
    this.dependsOn = dependsOn;
  }
  
  
  /**
   * Get a list of field names that the getMode method depends on. Some
   * implementations may compute this from the code of the validate method,
   * others will specify it explicitly.  
   *
   * @return list of field names
   */
  @Override
  public String[] getDependsOn () {
    return dependsOn;
  }

  
  private void convertException (Throwable throwable) throws UserEntryException {
    if (throwable instanceof UserEntryException) {
      throw (UserEntryException)throwable;
    } else if (throwable instanceof Exception) {
      System.out.println("convertException: ");
      throwable.printStackTrace();
      String msg = throwable.getMessage();
      if (msg == null || msg.equals("null")) {
        msg = CamelCase.toSentence(throwable.getClass().getSimpleName());
      }
      throw new UserEntryException(msg);
    } else {
      System.out.println("convertException: ");
      throwable.printStackTrace();
      String msg = throwable.getClass().getSimpleName();
      String msg1 = throwable.getMessage();
      if (msg1 != null) {
        msg += ": " + msg1;
      }
      throw new UserEntryException(msg);
    }
  }
  
  
  /**
   * Validates the dependent fields. This method will be called 
   * whenever any of the dependent fields change their value.
   * 
   * @throws a DataEntryException if the validation fails
   */
  @Override
  public void validate (Object instance) throws UserEntryException {
    if (method != null) {
      try {
        method.invoke(instance);
      } catch (IllegalArgumentException ex) {
        throw new RuntimeException(ex);
      } catch (IllegalAccessException ex) {
        throw new RuntimeException(ex);
      } catch (InvocationTargetException ex) {
        Throwable nested = ex.getCause();
        convertException(nested);
      }
    } else {
      try {
        runnable.run(instance);
      } catch (Exception ex) {
        convertException(ex);
      }
    }
  }
  
  
  @Override
  public boolean isSlow () {
    return isSlow;
  }

  
  @Override
  public int getOrder() {
    return dependsOn.length * 1000 + subOrder;
  }


  @Override
  public int compareTo(IValidationMethod other) {
    int n = getOrder() - other.getOrder();
    if (n == 0) {
      if (method != null && ((ValidationMethod)other).method != null) {
        return method.getName().compareTo(((ValidationMethod)other).method.getName());
      } else {
        return subOrder - ((ValidationMethod)other).subOrder;
      }
    } else {
      return n;
    }
  }


  @Override
  public int hashCode() {
    if (method != null) {
      return method.getName().hashCode();
    } else {
      return runnable.hashCode();
    }
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ValidationMethod)) {
      return false;
    }
    ValidationMethod other = (ValidationMethod)obj;
    if (method != null) {
      if (other.method == null) {
        return false;
      } else {
        if (method.getName().equals(other.method.getName())) {
          return true;
        }
      }
    } else {
      return this.runnable.equals(other.runnable);
    }
    return false;
  }
  
  
  @Override
  public String getMethodName () {
    return method.getName();
  }
  
  
  @Override
  public String toString () {
    StringBuilder buffer = new StringBuilder();
    buffer.append("ValidationMethod[");
    if (method != null) {
      buffer.append("methodName=");
      buffer.append(method.getName());
    }
    if (runnable != null) {
      buffer.append("runnable=");
      buffer.append(runnable.getClass().getName());
    }
    buffer.append(", order=" + getOrder());
    buffer.append("]");
    return buffer.toString();
  }
}
