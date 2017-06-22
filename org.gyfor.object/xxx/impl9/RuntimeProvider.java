package org.gyfor.object.plan.impl9;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.gyfor.object.plan.IRuntimeProvider;


public class RuntimeProvider implements IRuntimeProvider {

  private final String[] appliesTo;
  private final String[] dependsOn;
  private final Method method;

  
  public RuntimeProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    this.appliesTo = appliesTo;
    this.method = method;
   
    // Calculate dependencies
    List<String> dx = fieldDependency.getDependencies(klass.getName(), method.getName());
    this.dependsOn = dx.toArray(new String[dx.size()]);
  }

  
  public RuntimeProvider (String[] appliesTo) {
    this.appliesTo = appliesTo;
    this.method = null;
    this.dependsOn = new String[0];
  }


  
  /**
   * Get a list of XPaths expressions that identify the fields that this plan
   * applies to. All matching fields will use the same getDefaultValue method.
   * The list should never be empty, but there is no problem if it is. 
   * 
   * @return list of XPath expressions
   */
  @Override
  public String[] getAppliesTo() {
    return appliesTo;
  }


  @Override
  public boolean appliesTo(String name) {
    for (String target : appliesTo) {
      if (target.equals(name)) {
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Get a list of field names that the getDefaultValue method depends on. Some
   * implementations may compute this from the code of the getDefaultValue method,
   * others will specify it explicitly.  The names here are relative to the control
   * which contains the IIntialValuePlan.
   * 
   * @return list of field names
   */
  @Override
  public String[] getDependsOn() {
    return dependsOn;
  }

  
  @Override
  public boolean isRuntime() {
    return method != null;
  }
  
  
  @SuppressWarnings("unchecked")
  protected <T> T invokeRuntime(Object instance) {
    try {
      method.setAccessible(true);
      return (T)method.invoke(instance);
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

}
