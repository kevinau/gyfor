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


import java.util.Calendar;
import java.util.TimeZone;

import org.gyfor.object.type.IType;
import org.gyfor.time.DateFactory;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.Type;


public abstract class DateBasedType<T> extends Type<T> implements IType<T> {

  protected static final Calendar tzCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

  private static final String REQUIRED_MESSAGE = "Required";
  
  //private static final DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  
  public DateBasedType () {
  }
  
  
  private static String entryDateFormat (int year, int month, int day) {
    StringBuilder buffer = new StringBuilder(8);
    if (day < 10) {
      buffer.append('0');
    }
    buffer.append(day);
    buffer.append('/');
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    buffer.append('/');
    year = year % 100;
    if (year < 10) {
      buffer.append('0');
    }
    buffer.append(year);
    return buffer.toString();
  }

  
  private static String valueDateFormat (int year, int month, int day) {
    StringBuilder buffer = new StringBuilder(10);
    buffer.append(year);
    buffer.append('-');
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    buffer.append('-');
    if (day < 10) {
      buffer.append('0');
    }
    buffer.append(day);
    return buffer.toString();
  }

  
  /**
   * Parse a string that contains a date.  The string can contain a day, month and year, as follows:
   * <ul>
   * <li>If the fields are delimited by spaces or punctuation, or if the month is an alpha field:
   * <ul>
   * <li>The day can be one or two digits.</li>
   * <li>The month can be one or two digits, or alpha characters that describe a month.</li>
   * <li>The year can be two or four digits.</li>
   * </ul>
   * </li>
   * <li>If the fields are all numbers and are packed tight (no spaces):
   * <ul>
   * <li>The day must be two digits.</li>
   * <li>The month must be two digits.</li>
   * <li>The year can be two or four digits.</li>
   * </ul>
   * </li>
   * </ul>
   * <p>
   * Note.  There is a Javascript version of this method in jquery.ui.datepicker.  Any changes to this
   * method should be changed in jquery.ui.datepicker.
   * 
   * @param source
   * @param defaultDate
   * @param msg
   * @param resultDate
   * @param completion
   * @return
   */
  public int validate (String source, T fillDate, String[] msg, T[] resultDate, String[] completion) {
    int[] fillYMD = null;
    if (fillDate != null) {
      fillYMD = splitDate(fillDate);
    }
    int[] resultYMD = new int[3];
    int result = DateFactory.validate(source, fillYMD, msg, resultYMD, completion);
    if (result == DateFactory.OK) {
      resultDate[0] = createFromYearMonthDay(resultYMD[0], resultYMD[1], resultYMD[2]);
    }
    return result;
  }
  
  
  @Override
  public abstract T primalValue();
  
  
  @Override
  public String getRequiredMessage () {
    return REQUIRED_MESSAGE;
  }  
  
  
  @Override
  protected T createFromString (T fillValue, String source) throws UserEntryException {
    T dx = null;
    if (source.equals("0100-01-01") || source.equals("0000-01-01")) {
      dx = primalValue();
    } else {
      @SuppressWarnings("unchecked")
      T[] resultDate = (T[])new Object[1];
      String[] completion = new String[1];
      String[] msg = new String[1];
      int result = validate(source, fillValue, msg, resultDate, completion);
      switch (result) {
      case DateFactory.OK :
        dx = resultDate[0];
        break;
      case DateFactory.INCOMPLETE :
        throw new UserEntryException(msg[0], UserEntryException.Type.INCOMPLETE, completion[0]);
      case DateFactory.REQUIRED :
        throw new UserEntryException(msg[0], UserEntryException.Type.REQUIRED, completion[0]);
      case DateFactory.ERROR :
        throw new UserEntryException(msg[0]);
      }
    }
    return dx;
  }
  
  
  @Override
  public T createFromString (String source) throws UserEntryException {
    return createFromString(today(), source);
  }

  
  @Override
  public T newInstance (String source) {
    T dx;
    if (source.equals("0100-01-01") || source.equals("0000-01-01")) {
      dx = primalValue();
    } else {
      /* Source string is in the form yyyy-mm-dd */
      int year = Integer.parseInt(source.substring(0, 4));
      int month = Integer.parseInt(source.substring(5, 5 + 2));
      int day = Integer.parseInt(source.substring(8, 8 + 2));
      dx = createFromYearMonthDay(year, month, day);
    }
    return dx;
  }
  
  
  @Override
  public String toEntryString (T value, T fillValue) {
    if (value == null) {
      return "";
    }
    int[] components = splitDate(value);
    return entryDateFormat(components[0], components[1], components[2]);
  }


  @Override
  public String toValueString (T value) {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    int[] components = splitDate(value);
    return valueDateFormat(components[0], components[1], components[2]);
  }


  @Override
  public int getFieldSize () {
    return 12;
  }
  
  
  @Override
  protected abstract void validate (T date) throws UserEntryException;
  
  
  protected abstract T createFromYearMonthDay (int year, int month, int day);

  
  public abstract T today();
  
  
  protected abstract int[] splitDate (T date);
  

  @Override
  public int getBufferSize () {
    return Long.BYTES;
  }
  
  
  @Override
  public String getSQLType() {
    return "DATE";
  }

}
