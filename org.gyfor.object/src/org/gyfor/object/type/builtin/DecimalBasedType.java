/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.gyfor.object.type.builtin;

import org.gyfor.object.NumberSign;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.IScaleSettable;
import org.gyfor.object.type.ISignAndPrecisionSettable;
import org.gyfor.object.type.IType;


public abstract class DecimalBasedType<T> extends Type<T> implements IType<T>, ISignAndPrecisionSettable, IScaleSettable {

  private NumberSign sign;
  private int precision;
  private int decimals;

  private static final long[] limits = {
    9L,
    99L,
    999L,
    9999L,
    99999L,
    999999L,
    9999999L,
    99999999L,
    999999999L,
    9999999999L,
    99999999999L,
    999999999999L,
    9999999999999L,
    99999999999999L,
    999999999999999L,
    9999999999999999L,
    99999999999999999L,
    999999999999999999L,
    Long.MAX_VALUE,
  };
  
  
  protected DecimalBasedType () {
    this (NumberSign.UNSIGNED, 10, 0);
  }
  
  
  protected DecimalBasedType (int precision) {
    this (NumberSign.UNSIGNED, precision, 0);
  }
  
  
  protected DecimalBasedType (int precision, int decimals) {
    this (NumberSign.UNSIGNED, precision, decimals);
  }
  
  
  protected DecimalBasedType (NumberSign sign, int precision) {
    this (sign, precision, 0);
  }
  
  
  protected DecimalBasedType (NumberSign sign, int precision, int decimals) {
    this.sign = sign;
    this.precision = precision;
    this.decimals = decimals;
  }
  
  
  @Override
  public int getFieldSize () {
    // Precision includes the decimals
    int chars = precision;
    if (sign == NumberSign.UNSIGNED) {
      // No sign character
    } else {
      // Add a character for the sign
      chars++;
    }
    if (decimals > 0) {
      // Add one for the decimal point
      chars++;
    }
    return chars;
  }


  @Override
  public void setPrecision (int precision) {
    this.precision = precision;
  }
    
  
  @Override
  public void setSign (NumberSign sign) {
    this.sign = sign;
  }
    
  
  protected NumberSign getNumberSign () {
    return sign;
  }
  
  
  @Override
  public void setScale (int decimals) {
    this.decimals = decimals;
  }
  
  
  @Override
  public String getRequiredMessage() {
//    if (decimals == 0) {
//      if (sign == NumberSign.SIGNED) {
//        return "Number required (it may be signed, but with no decimal digits)";
//      } else {
//        return "Number required (it must be unsigned and with no decimal digits)";
//      }
//    } else {
//      if (sign == NumberSign.SIGNED) {
//        return "Number required (it may be signed and have up to " + decimals + " decimal digits)";
//      } else {
//        return "Number required (it must be unsigned but can have up to " + decimals + " decimal digits)";
//      }
//    }
    return "Required";
  }
  
  
  @Override
  public abstract T newInstance (String source);


  @Override
  public abstract T primalValue();


  protected String trimTrailingZeros (String source) {
    int length = getTrimmedLength(source);
    return source.substring(0, length);
  }

  
  @Override
  public String toEntryString(T value, T fillValue) {
    if (value == null) {
      return "";
    }
    return trimTrailingZeros(value.toString());
  }
  

  private int getTrimmedLength (String source) {
    int length = source.length();
    int n = source.lastIndexOf('.');
    if (n >= 0) {
      while (length > n && source.charAt(length - 1) == '0') {
        length--;
      }
      if (n == length - 1) {
        length--;
      }
    }
    return length;
  }
  
  
  protected void validateDecimalSource (String source) throws UserEntryException {
    int i = 0;
    int length = getTrimmedLength(source);
    int leadingDigits = precision - decimals;
    
    if (length == 0) {
      throw new UserEntryException(getRequiredMessage(), UserEntryException.Type.REQUIRED);
    }
    
    /* Get the sign, but don't check it yet. */
    if (i < length) {
      char signChar = source.charAt(i);
      switch (signChar) {
      case '+' :
      case '-' :
        if (sign == NumberSign.UNSIGNED) {
          throw new UserEntryException("no sign allowed");
        }
        i++;
        if (i == length) {
          String msg;
          if (sign == NumberSign.UNSIGNED) {
            msg = "not a decimal number";
            throw new UserEntryException(msg);
          } else {
            msg = "not a signed decimal number";
            throw new UserEntryException(msg, UserEntryException.Type.INCOMPLETE);
          }
        }
        break;
      }
    }
    /* Check the digits first.  What they have entered should always be
     * checked before reporting on what they have not entered. */
    int count = 0;
    int decimalOffset = 0;
    boolean decimalSeen = false;
    while (i < length) {
      char c = source.charAt(i);
      if (Character.isDigit(c)) {
        /* Don't count leading zero's */
        if ((c != '0' || count > 0) && !decimalSeen) {
          count++;
        }
      } else if (c == '.' && !decimalSeen) {
        decimalOffset = i + 1;
        decimalSeen = true;
      } else {
        String msg;
        if (sign == NumberSign.UNSIGNED) {
          msg = "not a decimal number";
        } else {
          msg = "not a signed decimal number";
        }
        throw new UserEntryException(msg);
      }
      i++;
    }
    if (count > leadingDigits) {
      throw new UserEntryException("no more than " + leadingDigits + " leading digits are allowed");
    }
    if (decimalSeen && length - decimalOffset > decimals) {
      if (decimals == 0) {
        throw new UserEntryException("no decimal digits are allowed");
      } else {
        throw new UserEntryException("no more than " + decimals + " decimal digits are allowed");
      }
    }
  }
  
  
  protected void validatePrecision (long value) throws UserEntryException {
    if (sign == NumberSign.UNSIGNED && value < 0) {
      throw new UserEntryException("a negative number is not allowed");
    }
    value = Math.abs(value);
    int i = 0;
    while (i < limits.length && value > limits[i]) {
      i++;
    }
    i++;
    int leftDigits = precision - decimals;
    if (i > leftDigits) {
      if (decimals == 0) {
        throw new UserEntryException("only " + leftDigits + " digits are allowed");
      } else {
        throw new UserEntryException("only " + leftDigits + " digits are allowed before the decimal point");
      }
    }
  }
  
  
  protected void validateDecimals (int actualDecimals) throws UserEntryException {
    if (actualDecimals > decimals) {
      if (decimals == 0) {
        throw new UserEntryException("no decimal digits are allowed");
      } else {
        throw new UserEntryException("no more than " + decimals + " decimal digits are allowed");
      }
    }
  }

  
  @Override
  public String toString () {
    return "DecimalBasedType[" + sign + "," + precision + "," + decimals + "]";
  }
  
  
  @Override
  public String getSQLType() {
    if (decimals > 0) {
      return "DECIMAL(" + precision + "," + decimals + ")";
    } else {
      return "DECIMAL(" + precision + ")";
    }
  }

  
  //public void validate (T value, boolean nullable, boolean creating) throws UserEntryException {
  //  if (value == null) {
  //    if (nullable) {
  //      return;
  //    } else {
  //      throw new UserEntryException(getRequiredMessage(), UserEntryException.Type.REQUIRED);
  //    }
  //  }
  //  validate (value);
    //BigDecimal dv = (BigDecimal)value;
    //if (sign == NumberSign.UNSIGNED && dv.compareTo(BigDecimal.ZERO) < 0) {
    //  throw new UserEntryException("negative number not allowed");
    //}
    //if (count > digits) {
    //  throw new UserEntryException("only " + digits + " digits allowed");
    //}
    //if (decimalSeen && length - decimalOffset > decimals) {
    //  throw new UserEntryException("only " + decimals + " decimal digits allowed");
    //}
    
    // check whole digits and decimal digits
  //}

}
