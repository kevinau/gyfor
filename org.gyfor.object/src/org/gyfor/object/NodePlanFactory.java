package org.gyfor.object;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.gyfor.object.plan.IClassPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.object.plan.impl.ArrayPlan;
import org.gyfor.object.plan.impl.EmbeddedPlan;
import org.gyfor.object.plan.impl.ItemPlan;
import org.gyfor.object.plan.impl.ListPlan;
import org.gyfor.object.plan.impl.ReferencePlan;
import org.gyfor.object.type.IType;
import org.gyfor.object.type.ItemTypeRegistry;

public class NodePlanFactory {

//  public static INodePlan getNodePlan (Class<?> klass) {
//    this (klass, entityName(klass), entityLabel(klass), entityEntryMode(klass), 0);
//  }
  
  public static INodePlan getNodePlan (IPlanContext context, Type fieldType, Field field, String name, EntryMode entryMode, int dimension, boolean optional) {
    INodePlan nodePlan;
    
    if (fieldType instanceof GenericArrayType) {
      Type type1 = ((GenericArrayType)fieldType).getGenericComponentType();
      nodePlan = new ArrayPlan(context, field, (Class<?>)type1, name, entryMode, dimension + 1);
    } else if (fieldType instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType)fieldType;
      Type type1 = ptype.getRawType();
      if (type1.equals(List.class)) {
        Type[] typeArgs = ptype.getActualTypeArguments();
        if (typeArgs.length != 1) {
          throw new IllegalArgumentException("List must have one, and only one, type parameter");
        }
        Type type2 = typeArgs[0];
        nodePlan = new ListPlan(context, field, (Class<?>)type2, name, entryMode, dimension + 1);
      } else {
        throw new IllegalArgumentException("Parameterized type that is not a List");
      }
    } else if (fieldType instanceof Class) {
      Class<?> klass = (Class<?>)fieldType;
      if (klass.isArray()) {
        Type type1 = klass.getComponentType();
        nodePlan = new ArrayPlan(context, field, (Class<?>)type1, name, entryMode, dimension + 1);
      } else {
        nodePlan = getNodePlanPart2(context, field, fieldType, name, entryMode, dimension);
      }
    } else {
      throw new IllegalArgumentException("Unsupported type: " + fieldType);
    }
    return nodePlan;

//    
//    
//    if (ItemTypeRegistry.isItemType(klass)) {
//      return new ItemPlan(klass, field, name, label, entryMode);
//    } else if (klass.isArray()) {
//      return new ArrayPlan(klass, field, name, label, entryMode, dimension);
//    } else {
//      
//    }
//    if (klass.isEnum()) {
//      
//    }
//    IClassPlan<T> plan = (IClassPlan<T>)planCache.get(klass);
//    if (plan == null) {
//      plan = new ClassPlan<T>(klass);
//      planCache.put(klass, plan);
//    }
//    return plan;
  }
  
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  static INodePlan getNodePlanPart2 (IPlanContext context, Field field, Type fieldType, String name, EntryMode entryMode, int dimension) {
    INodePlan nodePlan;
  
    // Is there a type declaration within the class
    Class<?> fieldClass = (Class<?>)fieldType;
    ItemField itemFieldAnn = field.getAnnotation(ItemField.class);
  
    // Is there a named IType for the field (via type parameter of the FormField annotation),
    // or does the field type match one of the build in field types
    IType type = ItemTypeRegistry.lookupType(fieldClass, itemFieldAnn);
    if (type != null) {
      nodePlan = new ItemPlan(field, name, entryMode, type);
    } else {
      // Is it a reference type (identified by the ManyToOne annotation).
      ManyToOne fkAnn = field.getAnnotation(ManyToOne.class);
      if (fkAnn != null) {
        nodePlan = new ReferencePlan(context, field, fieldClass, field.getName(), entryMode);
      } else {
        // A reference type can also be identified by the OneToOne annotation.
        OneToOne fkAnn1 = field.getAnnotation(OneToOne.class);
        if (fkAnn1 != null) {
          nodePlan = new ReferencePlan(context, field, fieldClass, field.getName(), entryMode);
        } else {
          // Is it a class type (identified by the Embedded annotation.  The class is traversed and all
          // members are considered as potential entry fields.
          boolean embdAnn = field.isAnnotationPresent(Embedded.class);
          if (embdAnn) {
            nodePlan = new EmbeddedPlan(context, field, fieldClass, field.getName(), entryMode);
          } else {
            // The Embeddable annotation on the field class also identifies a class type.
            boolean emblAnn = fieldClass.isAnnotationPresent(Embeddable.class);
            if (emblAnn) {
              nodePlan = new EmbeddedPlan(context, field, fieldClass, field.getName(), entryMode);
            } else {
              //If within a collection (array or list) any object that is not a item, is an embedded class type.
              if (dimension >= 0) {
                nodePlan = new EmbeddedPlan(context, field, fieldClass, field.getName(), entryMode);
                //buildObjectPlan(parent, field, field.getName(), fieldType, -1, entryMode, false);
              } else {
                // Otherwise, throw an error.
                throw new RuntimeException("Field type not recognised: " + name + " " + fieldType);
              }
            }
          }
        }
      }
    }
    return nodePlan;
  }
 
  
  @SuppressWarnings("unchecked")
  public static <T> IClassPlan<T> getClassPlan (T value) {
    return (IClassPlan<T>)getClassPlan(value.getClass());
  }
  
}
