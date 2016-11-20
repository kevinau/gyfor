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

import java.text.MessageFormat;
import java.util.Locale;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.RegexStringType;


public class PhoneNumberType extends RegexStringType {
    
  /** Regular expression string.  Made up of country code (digits starting with +), area code (digits enclosed
   *  in parenthesis ()), followed by the phone number proper.  The country and area codes are optional.  Spaces,
   *  periods and dashes may be used to space out the phone number. */
  //private static final String regex = "^(\\+[0-9]{1,3}[ \\.\\-]+)?(\\([0-9]+([ \\.\\-][0-9]+)*\\)[ \\.\\-]*)?[0-9]+([ \\.\\-][0-9]+)*$";
  private static final String regex = "^[0-9]+([ \\.\\-][0-9]+)*$";

  private static String[] a2Codes = {
    "US",
    "CA",
    "GB",
    "AU",
    "NZ",
  };
  
  private static String[] ixCodes = {
    "1",
    "1",
    "44",
    "61",
    "64",
  };
  
  private static int[] maxDigitsArray = {
    10,
    10,
    10, 
    10,
    10,
  };
  
  private static int defaultMaxDigits = 10;
  
  static {
    try {
    String country = Locale.getDefault().getCountry();
    for (int i = 0; i < a2Codes.length; i++) {
      if (a2Codes[i].equals(country)) {
        defaultMaxDigits = maxDigitsArray[i];
      }
    }
    } catch (Throwable x) {
      
    }
  }
  
  
  public PhoneNumberType () {
    super (18, regex, "phone number");
  }
  
  
  @Override
  protected String createFromString (String source, String fillValue) throws UserEntryException {
    String value = super.createFromString(source, fillValue);
    validate (value);
    return value;
  }
  
  
  @Override
  protected void validate (String value) throws UserEntryException {
    super.validate(value);
    int maxDigits = 10;
    int n = 0;
    if (value.charAt(0) == '+') {
      n = 1;
      while (n < value.length() && Character.isDigit(value.charAt(n))) {
        n++;
      }
      String code = value.substring(1, n);
      for (int i = 0; i < ixCodes.length; i++) {
        if (ixCodes[i].equals(code)) {
          maxDigits = maxDigitsArray[i];
        }
      }
    } else {
      maxDigits = defaultMaxDigits;
    }
    int digits = 0;
    boolean first = true;
    for (int i = n; i < value.length(); i++) {
      if (first && value.charAt(i) == '0') {
        first = false;
      } else {
        if (Character.isDigit(value.charAt(i))) {
          digits++;
          first = false;
        }
      }
    }
    if (digits > maxDigits) {
      String msg = MessageFormat.format("not a valid phone number (more than {0} digits)", Integer.toString(maxDigits));
      throw new UserEntryException(msg, false);
    }
  }
  
}
