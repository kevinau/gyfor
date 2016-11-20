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


import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.gyfor.object.type.IPatternSettable;
//import org.apache.regexp.RE;
//import org.apache.regexp.RESyntaxException;
import org.gyfor.object.TextCase;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.StringBasedType;


public class RegexStringType extends StringBasedType<String> implements IPatternSettable {
  
  private String pattern;
  private RE re;
  private String targetName;

  
  public RegexStringType (int maxSize, String pattern, String targetName) {
    super (maxSize);
    setPattern(pattern, targetName);
  }


  @Override
  public void setPattern (String pattern, String targetName) {
    this.pattern = pattern;
    this.targetName = targetName;
    try {
      this.re = new RE(pattern);
    } catch (RESyntaxException ex) {
      throw new RuntimeException(ex);
    }
    int x = 0;
    for (int i = 0; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      if (Character.isLowerCase(c)) {
        x |= 1;
      } else if (Character.isUpperCase(c)) {
        x |= 2;
      }
    }
    TextCase allowedCase;
    if (x == 1) {
      allowedCase = TextCase.LOWER;
    } else if (x == 2) {
      allowedCase = TextCase.UPPER;
    } else {
      allowedCase = TextCase.MIXED;
    }
    setAllowedCase(allowedCase);
  }
  
  
	public Object create (String source) {
		return source;
	}


  @Override
  protected void validate (String vs) throws UserEntryException {
    //System.out.println("vvvv validate: " + re + " " + vs);
    if (!re.match(vs)) {
      String m;
      if (targetName != null && targetName.length() > 0) {
        if (vs.length() == 0) {
          m = targetName + " is required";
        } else {
          m = "not a valid " + targetName;
        }
      } else {
        int n0 = 0;
        if (pattern.startsWith("^")) {
          n0++;
        } 
        int n1 = pattern.length();
        if (pattern.endsWith("$")) {
          n1--;
        }
        if (vs.length() == 0) {
          m = "entry matching '" + pattern.substring(n0, n1) + "' is required";
        } else {
          m = "entry does not match '" + pattern.substring(n0, n1) + "'";
        }
      }
      //System.out.println("vvvv " + m);
      throw new UserEntryException(m, re.getMatchExtent() == vs.length());
    }
  }


  @Override
  public String newInstance(String source) {
    return source;
  }
  
  
  @Override
  public String primalValue () {
    return "";
  }

}
