package org.plcore.userio.plan.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.PathParser;
import org.plcore.userio.plan.IRuntimeProvider;


public class RuntimeProvider implements IRuntimeProvider {

  private final IPathExpression[] appliesTo;
  private final IPathExpression[] dependsOn;
  private final Method method;
  private final Field field;

  
  public RuntimeProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    this.appliesTo = PathParser.parse(appliesTo);
    this.method = method;
    this.field = null;
   
    // Calculate dependencies
    List<String> dx = fieldDependency.getDependencies(klass.getName(), method.getName());
    this.dependsOn = PathParser.parse(dx);
  }

  
  public RuntimeProvider (Class<?> klass, FieldDependency fieldDependency, Field field, String[] appliesTo) {
    this.appliesTo = PathParser.parse(appliesTo);
    this.method = null;
    this.field = field;
   
    // Calculate dependencies
    List<String> dx = fieldDependency.getDependencies(klass.getName(), field.getName());
    this.dependsOn = PathParser.parse(dx);
  }

  
  public RuntimeProvider (String[] appliesTo) {
    this.appliesTo = PathParser.parse(appliesTo);
    this.method = null;
    this.field = null;
    this.dependsOn = new IPathExpression[0];
  }


  
  /**
   * Get a list of XPaths expressions that identify the fields that this plan
   * applies to. All matching fields will use the same getDefaultValue method.
   * The list should never be empty, but there is no problem if it is. 
   * 
   * @return list of XPath expressions
   */
  @Override
  public IPathExpression[] getAppliesTo() {
    return appliesTo;
  }


//  @Override
//  public boolean appliesTo(String name) {
//    for (String target : appliesTo) {
//      if (target.equals(name)) {
//        return true;
//      }
//    }
//    return false;
//  }
  
  
  /**
   * Get a list of field names that the getDefaultValue method depends on. Some
   * implementations may compute this from the code of the getDefaultValue method,
   * others will specify it explicitly.  The names here are relative to the control
   * which contains the IIntialValuePlan.
   * 
   * @return list of field names
   */
  @Override
  public IPathExpression[] getDependsOn() {
    return dependsOn;
  }

  
  @Override
  public boolean isRuntime() {
    return method != null || field != null;
  }
  
  
  @SuppressWarnings("unchecked")
  protected <X> X invokeRuntime(Object instance) {
    try {
      if (method != null) {
        method.setAccessible(true);
        return (X)method.invoke(instance);
      } else {
        field.setAccessible(true);
        return (X)field.get(instance);
      }
    } catch (SecurityException ex) {
      throw new RuntimeException(ex);
    } catch (IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException(ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override 
  public String toString () {
    StringBuilder s = new StringBuilder();
    s.append("RuntimeProvider(");
    if (method != null) {
      s.append(method.getName());
    }
    if (field != null) {
      s.append(field.getName());
    }
    s.append(",[");
    IPathExpression[] appliesTo = getAppliesTo();
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

}
