package org.gyfor.object.type;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gyfor.math.Decimal;
import org.gyfor.object.ItemField;
import org.gyfor.object.NumberSign;
import org.gyfor.object.TextCase;
import org.gyfor.object.type.builtin.BigDecimalType;
import org.gyfor.object.type.builtin.BigIntegerType;
import org.gyfor.object.type.builtin.BooleanType;
import org.gyfor.object.type.builtin.ByteType;
import org.gyfor.object.type.builtin.CharacterType;
import org.gyfor.object.type.builtin.DateType;
import org.gyfor.object.type.builtin.DecimalType;
import org.gyfor.object.type.builtin.DoubleType;
import org.gyfor.object.type.builtin.EntityLifeType;
import org.gyfor.object.type.builtin.EnumType;
import org.gyfor.object.type.builtin.FileContentType;
import org.gyfor.object.type.builtin.FloatType;
import org.gyfor.object.type.builtin.IntegerType;
import org.gyfor.object.type.builtin.LocalDateType;
import org.gyfor.object.type.builtin.LongType;
import org.gyfor.object.type.builtin.PathType;
import org.gyfor.object.type.builtin.ShortType;
import org.gyfor.object.type.builtin.SqlDateType;
import org.gyfor.object.type.builtin.StringType;
import org.gyfor.object.type.builtin.Type;
import org.gyfor.object.type.builtin.URLType;
import org.gyfor.object.type.builtin.VersionType;
import org.gyfor.object.value.EntityLife;
import org.gyfor.object.value.FileContent;
import org.gyfor.object.value.VersionTime;


public class ItemTypeRegistry {

  // private static class Entry<T> {
  // private Class<T> fieldClass;
  // private Class<? extends IType<T>> fieldTypeClass;
  //
  // private Entry(Class<T> fieldClass, Class<? extends IType<T>>
  // fieldTypeClass) {
  // this.fieldClass = fieldClass;
  // this.fieldTypeClass = fieldTypeClass;
  // }
  // }

  private static Map<Class<?>, Class<? extends IType<?>>> typeMap = new LinkedHashMap<>();

  static {
    typeMap.put(BigDecimal.class, BigDecimalType.class);
    typeMap.put(BigInteger.class, BigIntegerType.class);
    typeMap.put(Boolean.class, BooleanType.class);
    typeMap.put(Boolean.TYPE, BooleanType.class);
    typeMap.put(Byte.class, ByteType.class);
    typeMap.put(Byte.TYPE, ByteType.class);
    typeMap.put(Character.class, CharacterType.class);
    typeMap.put(Character.TYPE, CharacterType.class);
    typeMap.put(Date.class, DateType.class);
    typeMap.put(Decimal.class, DecimalType.class);
    // entries.put(Directory.class, DirectoryType.class);
    typeMap.put(Double.class, DoubleType.class);
    typeMap.put(Double.TYPE, DoubleType.class);
    // entries.put(EmailAddress.class, EmailAddressType.class);
    typeMap.put(EntityLife.class, EntityLifeType.class);
    typeMap.put(FileContent.class, FileContentType.class);
    typeMap.put(File.class, PathType.class);
    typeMap.put(Float.class, FloatType.class);
    typeMap.put(Float.TYPE, FloatType.class);
    // entries.put(ImageCode.class, ImageCodeType.class);
    typeMap.put(Integer.class, IntegerType.class);
    typeMap.put(Integer.TYPE, IntegerType.class);
    typeMap.put(LocalDate.class, LocalDateType.class);
    typeMap.put(Long.class, LongType.class);
    typeMap.put(Long.TYPE, LongType.class);
    // entries.put(Password.class, PasswordType.class);
    // entries.put(PhoneNumber.class, PhoneNumberType.class);
    typeMap.put(Short.class, ShortType.class);
    typeMap.put(Short.TYPE, ShortType.class);
    typeMap.put(java.sql.Date.class, SqlDateType.class);
    typeMap.put(String.class, StringType.class);
    typeMap.put(VersionTime.class, VersionType.class);
    typeMap.put(URL.class, URLType.class);
  }


  private static Class<?> getTypeClass(Class<?> nodeClass, ItemField fieldAnn) {
    // Find a type for this field.
    if (fieldAnn != null) {
      Class<?> typeClass = fieldAnn.type();

      // The following test is required because the simpler
      // typeClass.equals(Void.TYPE) does not work.
      // It always returns false.
      if (!typeClass.getName().equals("java.lang.Void")) {
        return typeClass;
      }
    }

    // TODO code value types are yet to be supported
    // For speed, try a direct map lookup (using equality of the fieldClass with
    // the map key)
    Class<?> typeClass = typeMap.get(nodeClass);
    if (typeClass != null) {
      return typeClass;
    } else {
      if (nodeClass.isEnum()) {
        return Enum.class;
      } else {
        // If we can't find a match by direct lookup, iterate through the map
        // and try isAssignableFrom
        for (Map.Entry<Class<?>, Class<? extends IType<?>>> entry : typeMap.entrySet()) {
          Class<?> mapClass = entry.getKey();
          if (mapClass.isAssignableFrom(nodeClass)) {
            typeClass = entry.getValue();
            return typeClass;
          }
        }
      }
      return null;
    }
  }


  /**
   * Get the item type for a node class, if null if this class does not identify
   * an item.
   * 
   * Implementation note: This method must match the following isItemType
   * method.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static IType<?> lookupType(Class<?> nodeClass, ItemField fieldAnn) {
    IType<?> type = null;

    // Find a type for this field.
    try {
      Class<?> typeClass = getTypeClass(nodeClass, fieldAnn);
      if (typeClass == null) {
        return null;
      }
      // TODO code value types are yet to be supported
      if (Enum.class.isInstance(typeClass)) {
        // Special case
        type = new EnumType(nodeClass);
      } else {
        type = (IType<?>)typeClass.newInstance();
      }
      if (nodeClass.isPrimitive()) {
        // We assume the found type is an instance of Type
        ((Type)type).setPrimitive(true);
      }
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }

    // ... and then set parameters from the ItemField annotation.
    if (fieldAnn != null && type instanceof Type) {
      if (type instanceof ILengthSettable) {
        int n = fieldAnn.length();
        if (n != -1) {
          ((ILengthSettable)type).setMaxLength(n);
        }
      }

      // The order of the following is important for integers. The following
      // combinations are allowed:
      // - min and max
      // - sign and max
      // - sign and precision
      // - precision (assumed to be signed)
      // To get this to work, precision and max come first. After that, min and
      // sign.
      if (type instanceof IPrecisionSettable) {
        int n = fieldAnn.precision();
        if (n != -1) {
          ((IPrecisionSettable)type).setPrecision(n);
        }
      }
      if (type instanceof IMaxSettable) {
        long max = fieldAnn.max();
        if (max != Long.MAX_VALUE) {
          ((IMaxSettable)type).setMax(max);
        }
      }
      if (type instanceof IMinSettable) {
        long min = fieldAnn.min();
        if (min != Long.MIN_VALUE) {
          ((IMinSettable)type).setMin(min);
        }
      }
      if (type instanceof ISignSettable) {
        NumberSign ns = fieldAnn.sign();
        if (ns != NumberSign.UNSPECIFIED) {
          ((ISignSettable)type).setSign(ns);
        }
      }
      if (type instanceof IScaleSettable) {
        int n = fieldAnn.scale();
        if (n != -1) {
          ((IScaleSettable)type).setScale(n);
        }
      }
      if (type instanceof IPatternSettable) {
        String pattern = fieldAnn.pattern();
        if (pattern.length() > 0) {
          // If pattern is specified, an error message can must also be
          // specified
          String errorMessage = fieldAnn.errorMessage();
          if (errorMessage.length() == 0) {
            errorMessage = null;
          }
          ((IPatternSettable)type).setPattern(pattern, errorMessage);
        }
      }
      if (type instanceof ICaseSettable) {
        TextCase xcase = fieldAnn.xcase();
        if (xcase != TextCase.UNSPECIFIED) {
          ((ICaseSettable)type).setAllowedCase(xcase);
        }
      }
    }
    // try {
    // // Try for a method, named by default, that provides a type.
    // Method method = parentClass.getDeclaredMethod(memberName);
    // // The field must be the type of the field
    // if (IType.class.isAssignableFrom(method.getReturnType()) &&
    // method.getParameterTypes().length == 0) {
    // int modifiers = method.getModifiers();
    // if (Modifier.isStatic(modifiers)) {
    // // The method must not be annotated with an TypeFor annotation. If it
    // is
    // so
    // annotated,
    // // it is not a type providing method by convention.
    // if (method.getAnnotation(TypeFor.class) == null) {
    // method.setAccessible(true);
    // return (IType<?>)method.invoke(null);
    // }
    // }
    // }
    // } catch (NoSuchMethodException ex) {
    // // Continue.
    // } catch (InvocationTargetException ex) {
    // throw new RuntimeException(ex);
    // } catch (IllegalAccessException ex) {
    // throw new RuntimeException(ex);
    // } catch (IllegalArgumentException ex) {
    // throw new RuntimeException(ex);
    // }
    //
    // try {
    // // Otherwise, try for a field
    // Field field = parentClass.getDeclaredField(memberName);
    // // The field must be an IType field
    // if (IType.class.isAssignableFrom(field.getType())) {
    // int modifiers = field.getModifiers();
    // if (Modifier.isStatic(modifiers)) {
    // // The field must not be annotated with an TypeFor annotation. If it is
    // so
    // annotated,
    // // it is not a type providing field by convention.
    // if (field.getAnnotation(TypeFor.class) == null) {
    // field.setAccessible(true);
    // return (IType<?>)field.get(null);
    // }
    // }
    // }
    // } catch (NoSuchFieldException ex) {
    // // Continue.
    // } catch (IllegalAccessException ex) {
    // throw new RuntimeException(ex);
    // } catch (IllegalArgumentException ex) {
    // throw new RuntimeException(ex);
    // }
    // }
    return type;
  }


  /**
   * Does this node class identify an item node. That is, is this node class an
   * atomic item.
   */
  public static boolean isItemType(Class<?> nodeClass) {
    ItemField fieldAnn = nodeClass.getAnnotation(ItemField.class);
    return getTypeClass(nodeClass, fieldAnn) != null;
  }

}
