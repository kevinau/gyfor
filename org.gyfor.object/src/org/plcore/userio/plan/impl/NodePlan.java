package org.plcore.userio.plan.impl;

import java.lang.annotation.Annotation;

import org.plcore.userio.EntryMode;
import org.plcore.userio.Mode;
import org.plcore.userio.Optional;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.MemberValueGetterSetter;

public abstract class NodePlan implements INodePlan {

  private final MemberValueGetterSetter field;
  
  private final String name;
  
  private EntryMode staticMode = EntryMode.UNSPECIFIED;
  
  private final boolean nullable;
  
  
  protected static String entityName (Class<?> entityClass) {
    String klassName = entityClass.getSimpleName();
    return Character.toLowerCase(klassName.charAt(0)) + klassName.substring(1);
  }
  
  
  protected static EntryMode entityEntryMode (Class<?> entityClass) {
    EntryMode entryMode = EntryMode.UNSPECIFIED;
    Mode modeAnn = entityClass.getAnnotation(Mode.class);
    if (modeAnn != null) {
      entryMode = modeAnn.value();
    }
    return entryMode;
  }
  
  
  private static boolean isNullable (MemberValueGetterSetter field) {
    if (field == null) {
      return false;
    } else {
      Optional nullableAnn = field.getAnnotation(Optional.class);
      if (nullableAnn == null) {
        return false;
      } else {
        return nullableAnn.value();
      }
    }
  }
  
  
  public NodePlan (MemberValueGetterSetter field, String name, EntryMode entryMode) {
    this.field = field;
    this.name = name;
    
    this.staticMode = entryMode;
    this.nullable = isNullable(field);
  }
  
  
  @Override
  public <X> X getFieldValue (Object instance) {
    return field.get(instance);
  }
  
  
  @Override
  public void setFieldValue (Object instance, Object value) {
    field.set(instance, value);
  }
  
  
  @Override
  public boolean isViewOnly () {
    return !field.isSettable();
  }
  
  
  @Override
  public String getName () {
    return name;
  }
  
  
  @Override
  public EntryMode getEntryMode () {
    return staticMode;
  }
 
  
  @Override 
  public void dump () {
    dump (0);
  }
  
  
  @Override
  public void dump (int level) {
    indent(level);
    System.out.println(this.toString());
  }
  
  @Override
  public boolean isNullable () {
    return nullable;
  }
  
  
  @Override
  public <A extends Annotation> A getAnnotation(Class<A> klass) {
    return field.getAnnotation(klass);
  }
  
  
//  @Override
//  public Method getSetter () {
//    return setter;
//  }
//  
//  
//  @Override
//  public Method getGetter () {
//    return getter;
//  }
  
  
  @Override
  public boolean isItem () {
    return false;
  }

  
  @Override
  public String toString () {
    return "NodePlan('" + name + "'," + staticMode + ")";
  }

}
