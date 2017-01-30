package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.gyfor.object.DefaultFor;
import org.gyfor.object.EntryMode;
import org.gyfor.object.FactoryFor;
import org.gyfor.object.ImplementationFor;
import org.gyfor.object.LabelFor;
import org.gyfor.object.MappedSuperclass;
import org.gyfor.object.Mode;
import org.gyfor.object.ModeFor;
import org.gyfor.object.NodePlanFactory;
import org.gyfor.object.NotItemField;
import org.gyfor.object.OccursFor;
import org.gyfor.object.Optional;
import org.gyfor.object.TypeFor;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.Validation;
import org.gyfor.object.plan.IContainerPlan;
import org.gyfor.object.plan.IItemPlan;
import org.gyfor.object.plan.INodePlan;
import org.gyfor.object.plan.IRuntimeDefaultProvider;
import org.gyfor.object.plan.IRuntimeFactoryProvider;
import org.gyfor.object.plan.IRuntimeImplementationProvider;
import org.gyfor.object.plan.IRuntimeLabelProvider;
import org.gyfor.object.plan.IRuntimeModeProvider;
import org.gyfor.object.plan.IRuntimeOccursProvider;
import org.gyfor.object.plan.IRuntimeTypeProvider;
import org.gyfor.object.plan.IValidationMethod;
import org.gyfor.object.type.IType;
import org.gyfor.util.CamelCase;


public class AugmentedClass<T> {

  private final Class<T> klass;

  private final Map<String, INodePlan> memberPlans = new LinkedHashMap<>();
  private final Map<String, Field> memberFields = new HashMap<>();

  private List<IRuntimeTypeProvider> runtimeTypeProviders = new ArrayList<>(0);
  private List<IRuntimeLabelProvider> runtimeLabelProviders = new ArrayList<>(0);
  private List<IRuntimeModeProvider> runtimeModeProviders = new ArrayList<>(0);
  private List<IRuntimeImplementationProvider> runtimeImplementationProviders = new ArrayList<>(0);
  private List<IRuntimeDefaultProvider> runtimeDefaultProviders = new ArrayList<>(0);
  private List<IRuntimeFactoryProvider> runtimeFactoryProviders = new ArrayList<>(0);
  private List<IRuntimeOccursProvider> runtimeOccursProviders = new ArrayList<>(0);
  //private List<IRuntimeFactoryProvider2> runtimeFactoryProviders2 = new ArrayList<>(0);
  private Set<IValidationMethod> validationMethods = new TreeSet<IValidationMethod>();


  protected static String entityName (Class<?> entityClass) {
    String klassName = entityClass.getSimpleName();
    return Character.toLowerCase(klassName.charAt(0)) + klassName.substring(1);
  }
  
  
  protected static String entityLabel (Class<?> entityClass) {
    String klassName = entityClass.getSimpleName();
    return CamelCase.toSentence(klassName);
  }
  
  
  protected static EntryMode entityEntryMode (Class<?> entityClass) {
    EntryMode entryMode = EntryMode.UNSPECIFIED;
    Mode modeAnn = entityClass.getAnnotation(Mode.class);
    if (modeAnn != null) {
      entryMode = modeAnn.value();
    }
    return entryMode;
  }
  

  public AugmentedClass (PlanContext context, Class<T> klass) {
    this.klass = klass;
    
//    Mode modeAnn = klass.getAnnotation(Mode.class);
//    if (modeAnn != null) {
//      entryMode = modeAnn.value();
//    } else {
//      entryMode = EntryMode.UNSPECIFIED;
//    }
    addClassFields (context, klass, true);
  }
  
  
  public String getClassName() {
    return klass.getCanonicalName();
  }
  
  
//  @SuppressWarnings("unchecked")
//  public static <T> IClassPlan<T> getClassPlan (Class<T> klass) {
//    String className = klass.getSimpleName();
//    IClassPlan<T> plan = (IClassPlan<T>)planCache.get(className);
//    if (plan == null) {
//      plan = new ClassPlan<T>(klass);
//      planCache.put(className, plan);
//    }
//    return plan;
//  }
//   
//  
//  @SuppressWarnings("unchecked")
//  public static <T> IClassPlan<T> getClassPlan (INodePlan parent, String pathName, String label, Class<T> klass, EntryMode entryMode) {
//    String className = klass.getSimpleName();
//    IClassPlan<T> plan = (IClassPlan<T>)planCache.get(className);
//    if (plan == null) {
//      plan = new ClassPlan<T>(parent, pathName, label, klass, entryMode);
//      planCache.put(className, plan);
//    }
//    return plan;
//  }
   
  
  public void addClassFields (PlanContext context, Class<?> klass, boolean include) {
    Field[] declaredFields = klass.getDeclaredFields();
    addClassFields2 (context, klass, declaredFields, include);
  }
  
  
  private void addClassFields2 (PlanContext context, Class<?> klass, Field[] fields, boolean include) {
    // Parse the class hierarchy recursively
    Class<?> superKlass = klass.getSuperclass();
    if (superKlass != null && !superKlass.equals(Object.class)) {
      MappedSuperclass msc = superKlass.getAnnotation(MappedSuperclass.class);
      addClassFields(context, superKlass, msc != null);
    }
    
    FieldDependency fieldDependency = new FieldDependency();
    fieldDependency.parseClass(klass.getName());

    if (include) {
//      Map<String, Field> lastEntryFields = new HashMap<String, Field>();

//      // Parse the declared fields of this class, first for 'last entry' fields
//      for (Field field : fields) {
//        if (field.isSynthetic()) {
//          // Synthetic fields cannot be form fields
//          continue;
//        }
//        int m = field.getModifiers();
//        if ((m & (Modifier.STATIC |Modifier.VOLATILE)) != 0) {
//          // Exclude static and volatile fields (but transient and final fields are not excluded)
//          continue;
//        }
//
//        // Remember the last entry fields
//        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
//        if (lastEntryForAnn != null) {
//          for (String name : lastEntryForAnn.value()) {
//            lastEntryFields.put(name, field);
//          }
//          continue;
//        }
//        
//        // add last entry fields named by convention
//        String name = field.getName();
//        if (name.endsWith("LastEntry")) {
//          String n = name.substring(0, name.length() - 9);
//          lastEntryFields.put(n, field);
//          continue;
//        }
//      }

      // And again for the fields themselves
      for (Field field : fields) {
        if (field.isSynthetic()) {
          // Synthetic fields cannot be form fields
          continue;
        }
        int m = field.getModifiers();
        if ((m & (Modifier.STATIC | Modifier.VOLATILE)) != 0) {
          // Exclude static and volatile fields (but transient and final fields are not excluded)
          continue;
        }

        NotItemField notFieldAnn = field.getAnnotation(NotItemField.class);
        if (notFieldAnn != null) {
          // Exclude fields annotated with @NotFormField
          continue;
        }
        
//        // Last entry fields are not form input fields
//        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
//        if (lastEntryForAnn != null && lastEntryForAnn.value().length > 0) {
//          continue;
//        } else {
//          if (field.getName().endsWith("LastEntry")) {
//            continue;
//          }
//          // unless they are annotated with an empty LastEntryFor
//        }
        
        // From here on in, all the fields are input fields.  It just depends on the
        // type as to whether the fields are embedded or not.
//        String label = null;
//        ItemField formFieldAnn = field.getAnnotation(ItemField.class);
//        if (formFieldAnn != null) {
//          label = formFieldAnn.label();
//        }
//        if (label == null || label.equals("\u0000")) {
//          label = CamelCase.toSentence(field.getName());
//        }
        
        EntryMode entryMode = EntryMode.UNSPECIFIED;
        if ((m & Modifier.FINAL) != 0) {
          entryMode = EntryMode.VIEW; 
        }
        Mode modeAnn = field.getAnnotation(Mode.class);
        if (modeAnn != null) {
          entryMode = modeAnn.value();
          if ((m & Modifier.FINAL) != 0 && entryMode == EntryMode.ENABLED) {
            throw new RuntimeException("Cannot set an entry mode of 'ENABLED' on final fields");
          }
        }
        
        boolean optional;
        if (field.getDeclaringClass().isPrimitive()) {
          // Primitives cannot be optional
          optional = false;
        } else {
          // If an Optional annotation exists set the optional value.
          Optional optionalAnn = field.getAnnotation(Optional.class);
          if (optionalAnn != null) {
            optional = optionalAnn.value();
          } else {
            optional = false;
          }
        }

//        Field lastEntryField = lastEntryFields.get(field.getName());

        Type type = field.getGenericType();
        String name = field.getName();
        
        INodePlan nodePlan = NodePlanFactory.getNodePlan(context, type, field, name, entryMode, -1, optional);
        memberPlans.put(name, nodePlan);
        memberFields.put(name, field);
      }
    }
    
    findTypeForAnnotations (klass, fields, fieldDependency);
    findLabelForAnnotations (klass, fields, fieldDependency);
    findModeForAnnotations (klass, fields, fieldDependency);
    findImplementationForAnnotations (klass, fields, fieldDependency);
    findDefaultForAnnotations (klass, fields, fieldDependency);
    findOccursForAnnotations (klass, fields, fieldDependency);
    findFactoryForAnnotations (klass, fieldDependency);
    findValidationMethods (klass, fieldDependency);
  }
    
  
//  public static INodePlan buildObjectPlan (INodePlan parent, Field field, String name, String label, Type fieldType, int dimension, EntryMode entryMode, boolean optional) {
//    INodePlan objPlan;
//    
//    if (fieldType instanceof GenericArrayType) {
//      Type type1 = ((GenericArrayType)fieldType).getGenericComponentType();
//      objPlan = new ArrayPlan(parent, field, name, label, (Class<?>)type1, dimension + 1, entryMode);
//    } else if (fieldType instanceof ParameterizedType) {
//      ParameterizedType ptype = (ParameterizedType)fieldType;
//      Type type1 = ptype.getRawType();
//      if (type1.equals(List.class)) {
//        Type[] typeArgs = ptype.getActualTypeArguments();
//        if (typeArgs.length != 1) {
//          throw new IllegalArgumentException("List must have one, and only one, type parameter");
//        }
//        Type type2 = typeArgs[0];
//        objPlan = new ListPlan(parent, field, name, label, (Class<?>)type2, dimension + 1, entryMode);
//      } else {
//        throw new IllegalArgumentException("Parameterized type that is not a List");
//      }
//    } else if (fieldType instanceof Class) {
//      Class<?> klass = (Class<?>)fieldType;
//      if (klass.isArray()) {
//        Type type1 = klass.getComponentType();
//        objPlan = new ArrayPlan(parent, field, name, label, (Class<?>)type1, dimension + 1, entryMode);
//      } else {
//        objPlan = fieldPlanDetail(parent, field, name, label, fieldType, dimension, entryMode, optional);
//      }
//    } else {
//      throw new IllegalArgumentException("Unsupported type: " + fieldType);
//    }
//    return objPlan;
//  }
//
//  
//  @SuppressWarnings({ "unchecked", "rawtypes" })
//  static INodePlan fieldPlanDetail (INodePlan parent, Field field, String name, String label, Type fieldType, int dimension, EntryMode entryMode, boolean optional) {
//    INodePlan objectPlan;
//    
//    // Is there a type declaration within the class
//    Class<?> fieldClass = (Class<?>)fieldType;
//    ItemField itemFieldAnn = field.getAnnotation(ItemField.class);
//    
//    // Is there a named IType for the field (via type parameter of the FormField annotation),
//    // or does the field type match one of the build in field types
//    IType type = ItemTypeRegistry.lookupType(fieldClass, itemFieldAnn);
//    if (type != null) {
//      objectPlan = new ItemPlan(parent, name, label, type, field, entryMode, optional);
//    } else {
//      // Is it a reference type (identified by the ManyToOne annotation).
//      ManyToOne fkAnn = field.getAnnotation(ManyToOne.class);
//      if (fkAnn != null) {
//        objectPlan = new ReferencePlan(parent, field.getName(), label, fieldClass, entryMode, fkAnn.optional());
//      } else {
//        // A reference type can also be identified by the OneToOne annotation.
//        OneToOne fkAnn1 = field.getAnnotation(OneToOne.class);
//        if (fkAnn1 != null) {
//          objectPlan = new ReferencePlan(parent, field.getName(), label, fieldClass, entryMode, fkAnn1.optional());
//        } else {
//          // Is it a class type (identified by the Embedded annotation.  The class is traversed and all
//          // members are considered as potential entry fields.
//          boolean embdAnn = field.isAnnotationPresent(Embedded.class);
//          if (embdAnn) {
//            objectPlan = new EmbeddedPlan(parent, field.getName(), label, fieldClass, entryMode);
//          } else {
//            // The Embeddable annotation on the field class also identifies a class type.
//            boolean emblAnn = fieldClass.isAnnotationPresent(Embeddable.class);
//            if (emblAnn) {
//              objectPlan = new EmbeddedPlan(parent, field.getName(), label, fieldClass, entryMode);
//            } else {
//              //If within a collection (array or list) any object that is not a field, is an embedded class type.
//              if (dimension >= 0) {
//                objectPlan = new EmbeddedPlan(parent, field.getName(), label, fieldClass, entryMode);
//                //buildObjectPlan(parent, field, field.getName(), fieldType, -1, entryMode, false);
//              } else {
//                // Otherwise, throw an error.
//                throw new RuntimeException("Field type not recognised: " + name + " " + fieldType);
//              }
//            }
//          }
//        }
//      }
//    }
//    return objectPlan;
//  }
// 
//   
//  public void walkClassPlan (Class<?> klass, WalkPlanTarget target) {
//    walkClassFields (klass, true, target);
//  }
//  
//  
//  private void walkClassFields (Class<?> klass, boolean include, WalkPlanTarget target) {
//    Field[] declaredFields = klass.getDeclaredFields();
//    walkClassFields2 (klass, declaredFields, include, target);
//  }
//  
//  
//  private void walkClassFields2 (Class<?> klass, Field[] fields, boolean include, WalkPlanTarget target) {
//    // Parse the class hierarchy recursively
//    Class<?> superKlass = klass.getSuperclass();
//    if (superKlass != null && !superKlass.equals(Object.class)) {
//      MappedSuperclass msc = superKlass.getAnnotation(MappedSuperclass.class);
//      walkClassFields(superKlass, msc != null, target);
//    }
//    
//    if (include) {
////      Map<String, Field> lastEntryFields = new HashMap<String, Field>();
//
////      // Parse the declared fields of this class, first for 'last entry' fields
////      for (Field field : fields) {
////        if (field.isSynthetic()) {
////          // Synthetic fields cannot be form fields
////          continue;
////        }
////        int m = field.getModifiers();
////        if ((m & (Modifier.STATIC |Modifier.VOLATILE)) != 0) {
////          // Exclude static and volatile fields (but transient and final fields are not excluded)
////          continue;
////        }
////
////        // Remember the last entry fields
////        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
////        if (lastEntryForAnn != null) {
////          for (String name : lastEntryForAnn.value()) {
////            lastEntryFields.put(name, field);
////          }
////          continue;
////        }
////        
////        // add last entry fields named by convention
////        String name = field.getName();
////        if (name.endsWith("LastEntry")) {
////          String n = name.substring(0, name.length() - 9);
////          lastEntryFields.put(n, field);
////          continue;
////        }
////      }
//
//      // And again for the fields themselves
//      for (Field field : fields) {
//        if (field.isSynthetic()) {
//          // Synthetic fields cannot be form fields
//          continue;
//        }
//        int m = field.getModifiers();
//        if ((m & (Modifier.STATIC | Modifier.VOLATILE)) != 0) {
//          // Exclude static and volatile fields (but transient and final fields are not excluded)
//          continue;
//        }
//
//        NotFormField notFieldAnn = field.getAnnotation(NotFormField.class);
//        if (notFieldAnn != null) {
//          // Exclude fields annotated with @NotFormField
//          continue;
//        }
//        
////        // Last entry fields are not form input fields
////        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
////        if (lastEntryForAnn != null && lastEntryForAnn.value().length > 0) {
////          continue;
////        } else {
////          if (field.getName().endsWith("LastEntry")) {
////            continue;
////          }
////          // unless they are annotated with an empty LastEntryFor
////        }
//        
//        // From here on in, all the fields are input fields.  It just depends on the
//        // type as to whether the fields are embedded or not.
//        EntryMode entryMode = EntryMode.UNSPECIFIED;
//        if ((m & Modifier.FINAL) != 0) {
//          entryMode = EntryMode.VIEW; 
//        }
//        Mode modeAnn = field.getAnnotation(Mode.class);
//        if (modeAnn != null) {
//          entryMode = modeAnn.value();
//          if ((m & Modifier.FINAL) != 0 && entryMode == EntryMode.ENTRY) {
//            throw new RuntimeException("Cannot set an entry mode of 'ENTRY' on final fields");
//          }
//        }
//        
//        boolean optional;
//        if (field.getDeclaringClass().isPrimitive()) {
//          // Primitives cannot be optional
//          optional = false;
//        } else {
//          // If an Optional annotation exists set the optional value.
//          Optional optionalAnn = field.getAnnotation(Optional.class);
//          if (optionalAnn != null) {
//            optional = optionalAnn.value();
//          } else {
//            optional = false;
//          }
//        }
//
////        Field lastEntryField = lastEntryFields.get(field.getName());
//
//        String name = field.getName();
//        
//        walkMemberPlan(name, field, -1, entryMode, optional, target);
//      }
//    }
//    
//    FieldDependency fieldDependency = new FieldDependency();
//    fieldDependency.parseClass(klass.getName());
//
//    findTypeForAnnotations (klass, fields, fieldDependency);
//    findLabelForAnnotations (klass, fields, fieldDependency);
//    findModeForAnnotations (klass, fields, fieldDependency);
//    findImplementationForAnnotations (klass, fields, fieldDependency);
//    findDefaultForAnnotations (klass, fields, fieldDependency);
//    findOccursForAnnotations (klass, fields, fieldDependency);
//    findFactoryForAnnotations (klass, fieldDependency);
//    findValidationMethods (klass, fieldDependency);
//  }
//      
//  
//  public static void walkMemberPlan (String name, Field field, int dimension, EntryMode entryMode, boolean optional, WalkPlanTarget target) {
//    Class<?> fieldClass = field.getType();
//    if (fieldClass.isArray()) {
//      Class<?> arrayClass = fieldClass.getComponentType();
//      target.array(name, arrayClass, dimension + 1, entryMode);
//    } else {
//      Type fieldType = field.getGenericType();
//      if (fieldType instanceof ParameterizedType) {
//        ParameterizedType ptype = (ParameterizedType)fieldType;
//        Type type1 = ptype.getRawType();
//        if (type1.equals(List.class)) {
//          Type[] typeArgs = ptype.getActualTypeArguments();
//          if (typeArgs.length != 1) {
//            throw new IllegalArgumentException("List must have one, and only one, type parameter");
//          }
//          Type type2 = typeArgs[0];
//          target.list(name, type2, field, dimension + 1, entryMode);
//        } else {
//          throw new IllegalArgumentException("Parameterized type that is not a List");
//        }
//      } else {
//        walkMemberPlanDetail(name, field, dimension, entryMode, optional, target);
//      }
//    }
//  }
//
//  
//   static void walkMemberPlanDetail (String name, Field field, int dimension, EntryMode entryMode, boolean optional, WalkPlanTarget target) {
//    // Is there a type declaration within the class
//    Class<?> fieldClass = field.getType();
//    ItemField formFieldAnn = field.getAnnotation(ItemField.class);
//    Column columnAnn = field.getAnnotation(Column.class);
//    
//    // Is there a named IType for the field (via type parameter of the FormField annotation),
//    // or does the field type match one of the build in field types
//    IType<?> type = BuiltinTypeRegistry.lookupType(fieldClass, formFieldAnn, columnAnn);
//    if (type != null) {
//      target.field(field.getName(), type, field, entryMode, optional);
//    } else {
//      // Is it a reference type (identified by the ManyToOne annotation).
//      ManyToOne fkAnn = field.getAnnotation(ManyToOne.class);
//      if (fkAnn != null) {
//        target.reference(field.getName(), fieldClass,  entryMode, fkAnn.optional());
//      } else {
//        // A reference type can also be identified by the OneToOne annotation.
//        OneToOne fkAnn1 = field.getAnnotation(OneToOne.class);
//        if (fkAnn1 != null) {
//          target.reference(field.getName(), fieldClass,  entryMode, fkAnn1.optional());
//        } else {
//          // Is it a class type (identified by the Embedded annotation.  The class is traversed and all
//          // members are considered as potential entry fields.
//          boolean embdAnn = field.isAnnotationPresent(Embedded.class);
//          if (embdAnn) {
//            target.embedded(field.getName(), fieldClass, entryMode);
//          } else {
//            // The Embeddable annotation on the field class also identifies a class type.
//            boolean emblAnn = fieldClass.isAnnotationPresent(Embeddable.class);
//            if (emblAnn) {
//              target.embedded(field.getName(), fieldClass, entryMode);
//            } else {
//              //If within a collection (array or list) any object that is not a field, is an embedded class type.
//              if (dimension >= 0) {
//                target.embedded(field.getName(), fieldClass, entryMode);
//              } else {
//                // Otherwise, throw an error.
//                throw new RuntimeException("Field type not recognised: " + name + " " + fieldClass);
//              }
//            }
//          }
//        }
//      }
//    }
//  }
// 
//   
//
  private void findTypeForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with TypeFor. 
    for (Field field : fields) {
      TypeFor typeFor = field.getAnnotation(TypeFor.class);
      if (typeFor != null) {
        // This field has been explicitly annotated as the type for some field or fields.
        String[] xpaths = typeFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            IType<?> type = (IType<?>)field.get(null);
            IRuntimeTypeProvider typeProvider = new RuntimeTypeProvider(type, xpaths);
            runtimeTypeProviders.add(typeProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // Look for methods annotated with TypeFor. 
    for (Method method : klass.getDeclaredMethods()) {
      TypeFor typeFor = method.getAnnotation(TypeFor.class);
      if (typeFor != null) {
        // This method has been explicitly annotated as the type for some field or fields.
        String[] xpaths = typeFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            IType<?> type = (IType<?>)method.invoke(null);
            IRuntimeTypeProvider typeProvider = new RuntimeTypeProvider(type, xpaths);
            runtimeTypeProviders.add(typeProvider);
          } else {
            IRuntimeTypeProvider typeProvider = new RuntimeTypeProvider(klass, fieldDependency, method, xpaths);
            runtimeTypeProviders.add(typeProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  private void findModeForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with ModeFor. 
    for (Field field : fields) {
      ModeFor modeFor = field.getAnnotation(ModeFor.class);
      if (modeFor != null) {
        // This field has been explicitly annotated as an mode for some field or fields.
        String[] xpaths = modeFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            EntryMode mode = (EntryMode)field.get(null);
            IRuntimeModeProvider modeProvider = new RuntimeModeProvider(mode, xpaths);
            runtimeModeProviders.add(modeProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
    // Look for methods annotated with ModeFor. 
    for (Method method : klass.getDeclaredMethods()) {
      ModeFor modeFor = method.getAnnotation(ModeFor.class);
      if (modeFor != null) {
        // This method has been explicitly annotated as the use for some field or fields.
        String[] xpaths = modeFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            EntryMode mode = (EntryMode)method.invoke(null);
            IRuntimeModeProvider modeProvider = new RuntimeModeProvider(mode, xpaths);
            runtimeModeProviders.add(modeProvider);
          } else {
            IRuntimeModeProvider modeProvider = new RuntimeModeProvider(klass, fieldDependency, method, xpaths);
            runtimeModeProviders.add(modeProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  private void findImplementationForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with ImplementationFor. 
    for (Field field : fields) {
      ImplementationFor modeFor = field.getAnnotation(ImplementationFor.class);
      if (modeFor != null) {
        // This field has been explicitly annotated as an mode for some field or fields.
        String[] xpaths = modeFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            Class<?> implClass = (Class<?>)field.get(null);
            IRuntimeImplementationProvider implProvider = new RuntimeImplementationProvider(implClass, xpaths);
            runtimeImplementationProviders.add(implProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
    // Look for methods annotated with ImplementationFor. 
    for (Method method : klass.getDeclaredMethods()) {
      ImplementationFor modeFor = method.getAnnotation(ImplementationFor.class);
      if (modeFor != null) {
        // This method has been explicitly annotated as the use for some field or fields.
        String[] xpaths = modeFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            Class<?> implClass = (Class<?>)method.invoke(null);
            IRuntimeImplementationProvider implProvider = new RuntimeImplementationProvider(implClass, xpaths);
            runtimeImplementationProviders.add(implProvider);
          } else {
            IRuntimeImplementationProvider implProvider = new RuntimeImplementationProvider(klass, fieldDependency, method, xpaths);
            runtimeImplementationProviders.add(implProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  private void findOccursForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with OccursFor. 
    for (Field field : fields) {
      OccursFor occursFor = field.getAnnotation(OccursFor.class);
      if (occursFor != null) {
        // This field has been explicitly annotated as the size of an array field or fields.
        String[] xpaths = occursFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            int size = (Integer)field.get(null);
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(size, xpaths);
            runtimeOccursProviders.add(occursProvider);
          } else {
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(field, xpaths);
            runtimeOccursProviders.add(occursProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
    // Look for methods annotated with OccursFor. 
    for (Method method : klass.getDeclaredMethods()) {
      OccursFor occursFor = method.getAnnotation(OccursFor.class);
      if (occursFor != null) {
        // This method has been explicitly annotated as the size of an array field or fields.
        String[] xpaths = occursFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            int size = (Integer)method.invoke(null);
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(size, xpaths);
            runtimeOccursProviders.add(occursProvider);
          } else {
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(klass, fieldDependency, method, xpaths);
            runtimeOccursProviders.add(occursProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  private void findLabelForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with LabelFor. 
    for (Field field : fields) {
      LabelFor labelFor = field.getAnnotation(LabelFor.class);
      if (labelFor != null) {
        // This field has been explicitly annotated as a label for some field or fields.
        String[] xpaths = labelFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            String label = (String)field.get(null);
            IRuntimeLabelProvider labelProvider = new RuntimeLabelProvider(label, xpaths);
            runtimeLabelProviders.add(labelProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
    // Look for methods annotated with LabelFor. 
    for (Method method : klass.getDeclaredMethods()) {
      LabelFor labelFor = method.getAnnotation(LabelFor.class);
      if (labelFor != null) {
        // This method has been explicitly annotated as the label for some field or fields.
        String[] xpaths = labelFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            String label = (String)method.invoke(null);
            IRuntimeLabelProvider labelProvider = new RuntimeLabelProvider(label, xpaths);
            runtimeLabelProviders.add(labelProvider);
          } else {
            IRuntimeLabelProvider labelProvider = new RuntimeLabelProvider(klass, fieldDependency, method, xpaths);
            runtimeLabelProviders.add(labelProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
  }
  
  
  private void findDefaultForAnnotations (Class<?> classClass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with DefaultFor. 
    for (Field field : fields) {
      DefaultFor defaultFor = field.getAnnotation(DefaultFor.class);
      if (defaultFor != null) {
        // This field has been explicitly annotated as the default for some field or fields.
        String[] xpaths = defaultFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            Object defaultValue = field.get(null);
            IRuntimeDefaultProvider defaultProvider = new RuntimeDefaultProvider(defaultValue, xpaths);
            runtimeDefaultProviders.add(defaultProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // Look for methods annotated with DefaultFor. 
    for (Method method : classClass.getDeclaredMethods()) {
      DefaultFor defaultFor = method.getAnnotation(DefaultFor.class);
      if (defaultFor != null) {
        // This method has been explicitly annotated as the default for some field or fields.
        String[] fieldNames = defaultFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            Object defaultValue = method.invoke(null);
            IRuntimeDefaultProvider defaultProvider = new RuntimeDefaultProvider(defaultValue, fieldNames);
            runtimeDefaultProviders.add(defaultProvider);
          } else {
//            Class<?>[] methodParams = method.getParameterTypes();
//            boolean isIndex;
//            if (methodParams.length == 0) {
//              isIndex = false;
//            } else if (methodParams.length == 1 && methodParams[0] == Integer.TYPE) {
//              isIndex = true;
//            } else {
//              throw new RuntimeException("DefaultFor method must have no parameters or a single int parameter");
//            }
            IRuntimeDefaultProvider defaultProvider = new RuntimeDefaultProvider(classClass, fieldDependency, method, fieldNames);
            runtimeDefaultProviders.add(defaultProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  private void findFactoryForAnnotations (Class<?> classClass, FieldDependency fieldDependency) {
    // Look for methods annotated with FactoryFor. 
    for (Method method : classClass.getDeclaredMethods()) {
      FactoryFor factoryFor = method.getAnnotation(FactoryFor.class);
      if (factoryFor != null) {
        // This method has been explicitly annotated as the variant for some field or fields.
        String[] xpaths = factoryFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            Object factoryValue = method.invoke(null);
            IRuntimeFactoryProvider factoryProvider = new RuntimeFactoryProvider(factoryValue, xpaths);
            runtimeFactoryProviders.add(factoryProvider);
          } else {
            String[] fieldNames = factoryFor.value();
            IRuntimeFactoryProvider factoryProvider = new RuntimeFactoryProvider(classClass, fieldDependency, method, fieldNames);
            runtimeFactoryProviders.add(factoryProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  private void findValidationMethods (Class<?> classClass, FieldDependency fieldDependency) {
    for (Method method : classClass.getDeclaredMethods()) {
      // Consider only methods with zero parameters, a void return type and non-static.
      if (method.getParameterTypes().length == 0 &&
          method.getReturnType().equals(Void.TYPE) &&
          !Modifier.isStatic(method.getModifiers())) {
        Validation validation = method.getAnnotation(Validation.class);
        if (validation != null) {
          boolean isSlow = validation.slow();
          if (throwsException(method)) {
            IValidationMethod validationMethod = new ValidationMethod(classClass, fieldDependency, method, isSlow);
            validationMethods.add(validationMethod);
          } else {
            throw new RuntimeException("Method with Validation annotation, but does not throw Exception");
          }
        } else {
          if (throwsUserEntryException(method)) {
            IValidationMethod validationMethod = new ValidationMethod(classClass, fieldDependency, method, false);
            validationMethods.add(validationMethod);
          }
        }
      }
    }
  }


  private static boolean throwsException (Method method) {
    Class<?>[] exceptions = method.getExceptionTypes();
    for (Class<?> ex : exceptions) {
      if (Exception.class.isAssignableFrom(ex)) {
        return true;
      }
    }
    return false;
  }

  
  private boolean throwsUserEntryException (Method method) {
    Class<?>[] exceptions = method.getExceptionTypes();
    for (Class<?> ex : exceptions) {
      if (UserEntryException.class.isAssignableFrom(ex)) {
        return true;
      }
    }
    return false;
  }

  
  public List<IRuntimeLabelProvider> getRuntimeLabelProviders() {
    return runtimeLabelProviders;
  }

  
  public List<IRuntimeModeProvider> getRuntimeModeProviders() {
    return runtimeModeProviders;
  }

  
  public List<IRuntimeImplementationProvider> getRuntimeImplementationProviders() {
    return runtimeImplementationProviders;
  }

  
  public List<IRuntimeDefaultProvider> getRuntimeDefaultProviders() {
    return runtimeDefaultProviders;
  }

  
  public List<IRuntimeFactoryProvider> getRuntimeFactoryProviders() {
    return runtimeFactoryProviders;
  }

  
//  public List<IRuntimeFactoryProvider2> getRuntimeFactoryProviders2() {
//    return runtimeFactoryProviders2;
//  }

  
  public List<IRuntimeTypeProvider> getRuntimeTypeProviders() {
    return runtimeTypeProviders;
  }

  
  public Set<IValidationMethod> getValidationMethods() {
    return validationMethods;
  }

  
  public List<IRuntimeOccursProvider> getRuntimeOccursProviders() {
    return runtimeOccursProviders;
  }

  
  @SuppressWarnings("unchecked")
  public <X extends INodePlan> X getMemberPlan(String name) {
    return (X)memberPlans.get(name);
  }


  public INodePlan[] getMemberPlans() {
    INodePlan[] mx = new INodePlan[memberPlans.size()];
    int i = 0;
    for (INodePlan m : memberPlans.values()) {
      mx[i++] = m;
    }
    return mx;
  }

 
  public Field getNodeField (String memberName) {
    return memberFields.get(memberName);
  }
  
  
  public void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }
  
  
  public void dump (int level) {
    indent(level);
    System.out.println("ClassPlan(" + klass.getName() + "[" + memberPlans.size() + "]," + super.toString() + ")");
    for (IRuntimeFactoryProvider factoryProvider : runtimeFactoryProviders) {
      indent(level + 1);
      System.out.println(factoryProvider);
    }
    for (Map.Entry<String, INodePlan> entry : memberPlans.entrySet()) {
      indent(level+ 1);
      System.out.println(entry.getKey() + ":");
      INodePlan member = entry.getValue();
      member.dump(level + 2);
    }
  }


  public Class<?> getSourceClass() {
    return klass;
  }


  public Object newInstance () {
    Object instance;
    try {
      instance = klass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    return instance;  
  }
  
  
  public Object newInstance (Object fromInstance) {
    Object toInstance = newInstance();
    for (INodePlan member : memberPlans.values()) {
      Object fromValue = member.getValue(fromInstance);

      if (member instanceof IItemPlan) {
        member.setValue(toInstance, fromValue);      
      } else if (member instanceof IContainerPlan) {
        Object newValue = ((IContainerPlan)member).newInstance(fromValue);
        member.setValue(toInstance, newValue);      
      } else {
        throw new RuntimeException("Non-supported node plan: " + member);
      }
    }
    return toInstance;
  }


//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> itemPlans) {
//    for (INodePlan nodePlan : memberPlans.values()) {
//      nodePlan.accumulateTopItemPlans(itemPlans);
//    }
//  }


//  @Override
//  public Object getMemberValue(Object instance, String name) {
//    Field field = memberFields.get(name);
//    Object value;
//    try {
//      field.setAccessible(true);
//      value = field.get(instance);
//    } catch (IllegalArgumentException | IllegalAccessException ex) {
//      throw new RuntimeException(ex);
//    }
//    return value;
//  }
//
//
//  @Override
//  public void setMemberValue(Object instance, String name, Object value) {
//    Field field = memberFields.get(name);
//    try {
//      field.setAccessible(true);
//      field.set(instance, value);
//    } catch (IllegalArgumentException | IllegalAccessException ex) {
//      throw new RuntimeException(ex);
//    }
//  }


//  @Override
//  public IObjectModel buildModel(IForm<?> form, IObjectModel parent, IContainerReference container) {
//    return new ClassModel(form, parent, container, this);
//  }


//  @Override
//  public T newInstance() {
//    T instance;
//    try {
//      instance = klass.newInstance();
//    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
//      throw new RuntimeException(ex);
//    }
//    return instance;
//  }

}
