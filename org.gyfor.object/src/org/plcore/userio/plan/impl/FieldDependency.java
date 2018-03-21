package org.plcore.userio.plan.impl;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache BCEL" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache BCEL", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.plcore.userio.plan.IFieldDependency;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.classfile.Code;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.util.ByteSequence;

/**
 * Utility functions that do not really belong to any class in particular.
 * 
 * @author <A HREF="mailto:markus.dahm@berlin.de">M. Dahm</A>
 */
public class FieldDependency implements IFieldDependency {
  /*
   * The `WIDE' instruction is used in the byte code to allow 16-bit wide
   * indices for local variables. This opcode precedes an `ILOAD', e.g.. The
   * opcode immediately following takes an extra byte which is combined with the
   * following byte to form a 16-bit value.
   */
  private boolean wide;

  private Map<String, List<String>> methodDependsOn;
  private Map<String, List<String>> methodProvides;

  // /private Map<String, List<String>> fieldDependsOn;

  /**
   * Disassemble a byte array of JVM byte codes starting from code line `index'
   * and return the disassembled string representation. Decode only `num'
   * opcodes (including their operands), use -1 if you want to decompile
   * everything.
   * 
   * @param code
   *          byte code array
   * @param constant_pool
   *          Array of constants
   * @param index
   *          offset in `code' array <EM>(number of opcodes, not bytes!)</EM>
   * @param length
   *          number of opcodes to decompile, -1 for all
   * @param verbose
   *          be verbose, e.g. print constant pool index
   * @returns String representation of byte codes
   */
  public final void parseMethod(String methodName, byte[] code, ConstantPool constant_pool, int index, int length) {
    // sufficient
    List<String> dependsOn = new ArrayList<String>();
    List<String> provides = new ArrayList<String>();

    ByteSequence stream = new ByteSequence(code);

    try {
      for (int i = 0; i < index; i++) {
        parseOp(methodName, stream, constant_pool, dependsOn, provides);
      }
      for (int i = 0; stream.available() > 0; i++) {
        if ((length < 0) || (i < length)) {
          parseOp(methodName, stream, constant_pool, dependsOn, provides);
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Byte code error: " + e);
    }
    methodDependsOn.put(methodName, dependsOn);
    methodProvides.put(methodName, provides);
  }

  /**
   * Disassemble a stream of byte codes and return the string representation.
   * 
   * @param bytes
   *          stream of bytes
   * @param constant_pool
   *          Array of constants
   * @param verbose
   *          be verbose, e.g. print constant pool index
   * @returns String representation of byte code
   */
  private final void parseOp(String methodName, ByteSequence bytes, ConstantPool constant_pool, List<String> methodDependsOn, List<String> methodProvides) throws IOException {
    short opcode = (short)bytes.readUnsignedByte();
    int low, high, npairs;
    int index;
    int[] match, jumpTable;
    int noPadBytes = 0, offset;
    String fieldName = null;
    String fieldClass = null;
    String fieldFQN = null;
    Constant c = null;
    Constant c2 = null;

    /*
     * Special case: Skip (0-3) padding bytes, i.e., the following bytes are
     * 4-byte-aligned
     */
    if ((opcode == Constants.TABLESWITCH) || (opcode == Constants.LOOKUPSWITCH)) {
      int remainder = bytes.getIndex() % 4;
      noPadBytes = (remainder == 0) ? 0 : 4 - remainder;

      for (int i = 0; i < noPadBytes; i++) {
        byte b;
        if ((b = bytes.readByte()) != 0) {
          System.err.println("Warning: Padding byte != 0 in " + Constants.OPCODE_NAMES[opcode] + ":" + b);
        }
      }
      // Both cases have a field default_offset in common
      bytes.readInt();
    }

    switch (opcode) {
    /*
     * Table switch has variable length arguments.
     */
    case Constants.TABLESWITCH:
      low = bytes.readInt();
      high = bytes.readInt();
      offset = bytes.getIndex() - 12 - noPadBytes - 1;
      jumpTable = new int[high - low + 1];
      for (int i = 0; i < jumpTable.length; i++) {
        jumpTable[i] = offset + bytes.readInt();
      }
      break;
    /*
     * Lookup switch has variable length arguments.
     */
    case Constants.LOOKUPSWITCH:
      npairs = bytes.readInt();
      offset = bytes.getIndex() - 8 - noPadBytes - 1;

      match = new int[npairs];
      jumpTable = new int[npairs];
      for (int i = 0; i < npairs; i++) {
        match[i] = bytes.readInt();
        jumpTable[i] = offset + bytes.readInt();
      }
      break;

    /*
     * Two address bytes + offset from start of byte stream form the jump target
     */
    case Constants.GOTO:
    case Constants.IFEQ:
    case Constants.IFGE:
    case Constants.IFGT:
    case Constants.IFLE:
    case Constants.IFLT:
    case Constants.JSR:
    case Constants.IFNE:
    case Constants.IFNONNULL:
    case Constants.IFNULL:
    case Constants.IF_ACMPEQ:
    case Constants.IF_ACMPNE:
    case Constants.IF_ICMPEQ:
    case Constants.IF_ICMPGE:
    case Constants.IF_ICMPGT:
    case Constants.IF_ICMPLE:
    case Constants.IF_ICMPLT:
    case Constants.IF_ICMPNE:
      bytes.readShort();
      break;

    /*
     * 32-bit wide jumps
     */
    case Constants.GOTO_W:
    case Constants.JSR_W:
      bytes.readInt();
      break;

    /*
     * Index byte references local variable (register)
     */
    case Constants.ALOAD:
    case Constants.ASTORE:
    case Constants.DLOAD:
    case Constants.DSTORE:
    case Constants.FLOAD:
    case Constants.FSTORE:
    case Constants.ILOAD:
    case Constants.ISTORE:
    case Constants.LLOAD:
    case Constants.LSTORE:
    case Constants.RET:
      if (wide) {
        bytes.readUnsignedShort();
        wide = false; // Clear flag
      } else {
        bytes.readUnsignedByte();
      }
      break;

    /*
     * Remember wide byte which is used to form a 16-bit address in the
     * following instruction. Relies on that the method is called again with the
     * following opcode.
     */
    case Constants.WIDE:
      wide = true;
      break;

    /*
     * Array of basic type.
     */
    case Constants.NEWARRAY:
      bytes.readByte();
      break;

    /*
     * Access class fields.
     */
    case Constants.GETSTATIC:
    case Constants.PUTSTATIC:
      index = bytes.readUnsignedShort();
      break;
    /*
     * Access object fields.
     */
    case Constants.GETFIELD:
      index = bytes.readUnsignedShort();
      c = constant_pool.getConstant(index, Constants.CONSTANT_Fieldref);
      fieldClass = constant_pool.constantToString(((ConstantCP) c).getClassIndex(), Constants.CONSTANT_Class);

      c2 = constant_pool.getConstant(((ConstantCP) c).getNameAndTypeIndex(), Constants.CONSTANT_NameAndType);
      fieldName = constant_pool.constantToString(((ConstantNameAndType) c2).getNameIndex(), Constants.CONSTANT_Utf8);

      fieldFQN = fieldClass + "." + fieldName;
      if (!methodDependsOn.contains(fieldFQN)) {
        methodDependsOn.add(fieldFQN);
      }
      break;
    /*
     * Access object fields.
     */
    case Constants.PUTFIELD:
      index = bytes.readUnsignedShort();
      c = constant_pool.getConstant(index, Constants.CONSTANT_Fieldref);
      fieldClass = constant_pool.constantToString(((ConstantCP) c).getClassIndex(), Constants.CONSTANT_Class);

      c2 = constant_pool.getConstant(((ConstantCP) c).getNameAndTypeIndex(), Constants.CONSTANT_NameAndType);
      fieldName = constant_pool.constantToString(((ConstantNameAndType) c2).getNameIndex(), Constants.CONSTANT_Utf8);

      fieldFQN = fieldClass + "." + fieldName;
      if (!methodProvides.contains(fieldFQN)) {
        methodProvides.add(fieldFQN);
      }
      ///List<String> dependsOn = fieldDependsOn.get(fieldFQN);
      ///if (dependsOn == null) {
      ///  dependsOn = new ArrayList<String>();
      ///  fieldDependsOn.put(fieldFQN, dependsOn);
      ///}
      ///if (!dependsOn.contains(methodName)) {
      ///  dependsOn.add(methodName);
      ///}
      break;
    /*
     * Operands are references to classes in constant pool
     */
    case Constants.NEW:
    case Constants.CHECKCAST:
    case Constants.INSTANCEOF:
      index = bytes.readUnsignedShort();
      break;
    /*
     * Operands are references to methods in constant pool
     */
    case Constants.INVOKESPECIAL:
    case Constants.INVOKESTATIC:
    case Constants.INVOKEVIRTUAL:
      index = bytes.readUnsignedShort();
      break;

    case Constants.INVOKEINTERFACE:
      index = bytes.readUnsignedShort();
      bytes.readUnsignedByte(); // historical, redundant
      bytes.readUnsignedByte(); // Last byte is a reserved space
      break;
    /*
     * Operands are references to items in constant pool
     */
    case Constants.LDC_W:
    case Constants.LDC2_W:
      index = bytes.readUnsignedShort();
      break;

    case Constants.LDC:
      index = bytes.readUnsignedByte();
      break;
    /*
     * Array of references.
     */
    case Constants.ANEWARRAY:
      index = bytes.readUnsignedShort();
      break;
    /*
     * Multidimensional array of references.
     */
    case Constants.MULTIANEWARRAY:
      index = bytes.readUnsignedShort();
      bytes.readUnsignedByte();
      break;
    /*
     * Increment local variable.
     */
    case Constants.IINC:
      if (wide) {
        bytes.readUnsignedShort();
        bytes.readShort();
        wide = false;
      } else {
        bytes.readUnsignedByte();
        bytes.readByte();
      }
      break;

    default:
      if (Constants.NO_OF_OPERANDS[opcode] > 0) {
        for (int i = 0; i < Constants.TYPE_OF_OPERANDS[opcode].length; i++) {
          switch (Constants.TYPE_OF_OPERANDS[opcode][i]) {
          case Constants.T_BYTE:
            break;
          case Constants.T_SHORT:
            break;
          case Constants.T_INT:
            break;
          default: // Never reached
            throw new RuntimeException("Unreachable default case reached!");
          }
        }
      }
    }
  }

  public void parseClass(String className) {
    try {
      Class<?> cx = Class.forName(className);
      parseClass(cx);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public void parseClass(Class<?> cx) {
    JavaClass klass = Repository.lookupClass(cx);

    wide = false;
    methodDependsOn = new HashMap<String, List<String>>();
    methodProvides = new HashMap<String, List<String>>();
    // /fieldDependsOn = new HashMap<String, List<String>>();

    for (Method method : klass.getMethods()) {
      if (!method.isStatic() && !method.isNative() && !method.isAbstract()) {
        Code code = method.getCode();
        String methodFQN = klass.getClassName() + "." + method.getName();
        parseMethod(methodFQN, code.getCode(), method.getConstantPool(), 0, code.getLength());
      }
    }
  }

  
  private void getDependencies(int prefixLength, String methodName, List<String> fieldList) {
    List<String> fields = methodDependsOn.get(methodName);
    if (fields != null) {
      for (String fieldName : fields) {
        fieldName = fieldName.substring(prefixLength);
        if (!fieldList.contains(fieldName)) {
          fieldList.add(fieldName);
          for (Map.Entry<String, List<String>> entry : methodProvides.entrySet()) {
            if (entry.getValue().contains(fieldName)) {
              getDependencies(prefixLength, entry.getKey(), fieldList);
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public List<String> getDependencies(String className, String methodName) {
    String fqMethodName = className + "." + methodName;
    List<String> fieldList = new ArrayList<String>();
    getDependencies(className.length() + 1, fqMethodName, fieldList);
    return fieldList;
  }

  
  /** Return a list of fields that a named method is dependent on.
   * 
   * @param methodName - a fully qualified method name
   * @return list of fully qualified field names
   */
  public List<String> getDependsOn(String methodName) {
    return methodDependsOn.get(methodName);
  }

  
  /** Return a list of fields that a named method provides.
   * 
   * @param methodName - a fully qualified method name
   * @return list of fully qualified field names
   */
  public List<String> getProvides(String methodName) {
    return methodProvides.get(methodName);
  }

  public static void main(String[] args) throws ClassNotFoundException {
    JavaClass klass = Repository.lookupClass("org.geckosoftware.formEditor.Form3");
    for (Method mx : klass.getMethods()) {
      String x = Utility.codeToString(mx.getCode().getCode(), mx.getConstantPool(), 0, mx.getNameIndex());
      System.out.println(x);
    }
    FieldDependency utility = new FieldDependency();
    utility.parseClass("org.geckosoftware.formeditor.Form3");
    System.out.println(utility.methodDependsOn);
    System.out.println(utility.methodProvides);
    List<String> fx = utility.getDependsOn(klass.getClassName() + "." + "getF3");
    for (String f : fx) {
      System.out.println("? = " + f);
    }
    List<String> fz = utility.getProvides(klass.getClassName() + "." + "getF3");
    for (String f : fz) {
      System.out.println(f + " = ?");
    }
  }
}