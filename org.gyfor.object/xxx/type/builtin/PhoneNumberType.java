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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gyfor.object.TextCase;
import org.gyfor.object.UserEntryException;


public class PhoneNumberType extends StringType {
    
  private static String[] a2Codes = {
      "US",
      "CA",
      "GB",
      "AU",
      "NZ",
    };
    
  private static String[] ixCodes = {
    "+1",
    "+1",
    "+44",
    "+61",
    "+64",
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
    super (18, TextCase.MIXED);
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
    
    // Clean string, removing padding characters
    StringBuilder vx = new StringBuilder();
    for (int c : value.toCharArray()) {
      if (c != ' ' && c != '-') {
        vx.append((char)c);
      }
    }
    value = vx.toString();
    
    Pattern pattern = Pattern.compile("(\\+\\d{1,3})?(\\(\\d+\\))?\\d+");
    Matcher matcher = pattern.matcher(value);
    if (!matcher.matches()) {
      boolean complete = matcher.hitEnd();
      throw new UserEntryException("Not a valid phone number", complete);
    }

    int maxDigits = 10;
    int n = 0;
    if (value.charAt(0) == '+') {
      for (int i = 0; i < ixCodes.length; i++) {
        if (value.startsWith(ixCodes[i])) {
          maxDigits = maxDigitsArray[i];
          n = ixCodes[i].length();
          break;
        }
      }
    } else {
      maxDigits = defaultMaxDigits;
    }
    
    int digits = 0;
    for (int i = n; i < value.length(); i++) {
      if (value.charAt(i) != '(' && value.charAt(i) != ')') {
        digits++;
      }
    }
    if (digits > maxDigits) {
      String msg = MessageFormat.format("Not a valid phone number (more than {0} digits)", Integer.toString(maxDigits));
      throw new UserEntryException(msg, false);
    }
  }
  
}
