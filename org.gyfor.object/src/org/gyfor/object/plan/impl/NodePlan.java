package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;

import org.gyfor.object.EntryMode;
import org.gyfor.object.Mode;
import org.gyfor.object.Optional;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.INodePlan;

public abstract class NodePlan implements INodePlan {

  private final Field field;
  
  private final String name;
  
  private EntryMode staticMode = EntryMode.UNSPECIFIED;
  
  private final boolean nullable;
  
  private INodePlan parent;
  
  private String qualifiedName;
  
  
  @Override
  public void setParent (INodePlan parent) {
    this.parent = parent;
  }
  
  
  @Override
  public INodePlan getParent() {
    return parent;
  }
  
  
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
  
  
  private static boolean isNullable (Field field) {
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
  
  
  public NodePlan (Field field, String name, EntryMode entryMode) {
    this.field = field;
    if (field != null) {
      field.setAccessible(true);
    }
    this.name = name;
    
    this.staticMode = entryMode;
    this.nullable = isNullable(field);
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public <X> X getFieldValue (Object instance) {
    if (field == null) {
      throw new RuntimeException("Cannot invoke getValue on Entity level plan");
    }
    try {
      return (X)field.get(instance);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void setFieldValue (Object instance, Object value) {
    if (field == null) {
      throw new RuntimeException("Cannot invoke setValue on Entity level plan");
    }
    try {
      field.set(instance, value);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public String getName () {
    return name;
  }
  
  
  private static void buildQualifiedName (INodePlan nodePlan, StringBuilder builder, boolean[] isTop) {
    INodePlan parent = nodePlan.getParent();
    if (parent == null) {
      if (nodePlan instanceof IEntityPlan) {
        builder.append(((IEntityPlan<?>)nodePlan).getClassName());
      } else {
        throw new RuntimeException("Parent of " + nodePlan + " is null, but this is not an IEntityPlan");
      }
      isTop[0] = true;
    } else {
      buildQualifiedName(parent, builder, isTop);
      if (isTop[0]) {
        builder.append('#');
      } else {
        builder.append('.');
      }
      builder.append(nodePlan.getName());
    }
  }
  

  private String buildQualifiedName () {
    StringBuilder builder = new StringBuilder();
    boolean[] isTop = new boolean[1];
    buildQualifiedName(this, builder, isTop);
    return builder.toString();
  }
  
  
  @Override
  public String getQualifiedName() {
    if (qualifiedName == null) {
      qualifiedName = buildQualifiedName();
    }
    return qualifiedName;
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
  public Field getField () {
    return field;
  }
  
  
  @Override
  public boolean isItem () {
    return false;
  }

  
  @Override
  public String toString () {
    return "NodePlan(" + name + "'," + staticMode + ")";
  }

}
