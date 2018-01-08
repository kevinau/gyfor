package org.gyfor.object.type.builtin;

import org.gyfor.object.TextCase;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.ICaseSettable;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.gyfor.util.SimpleBuffer;


public class CharacterType extends Type<Character> implements ICaseSettable {

  private TextCase allowedCase;

  
  public CharacterType() {
    this.allowedCase = TextCase.MIXED;
  }

  
  public CharacterType(TextCase allowedCase) {
    this.allowedCase = allowedCase;
  }


  @Override
  public String toEntryString(Character value, Character fillValue) {
    if (value == null) {
      return "";
    }
    if (value.charValue() == 0) {
      return "";
    } else {
      return value.toString();
    }
  }
  

  @Override
  public Character createFromString (Character fillValue, boolean nullable, boolean creating, String source) throws UserEntryException {
    source = source.trim();
    if (source.length() == 0) {
      if (nullable) {
        return null;
      } else {
        // A character can be blank
      }
    }
    return createFromString(source);
  }
  

  @Override
  public Character createFromString(String source) throws UserEntryException {
    switch (allowedCase) {
    case UPPER:
      source = source.toUpperCase();
      break;
    case LOWER:
      source = source.toLowerCase();
      break;
    case MIXED :
    case UNSPECIFIED :
      break;
    }
    String s = source.trim();
    int n = s.length();
    if (n > 1) {
      throw new UserEntryException("more than 1 character");
    }
    if (n == 0) {
      return '\0';
    } else {
      return s.charAt(0);
    }
  }

  
  @Override
  public void setAllowedCase (TextCase allowedCase) {
    this.allowedCase = allowedCase;
  }

  
  @Override
  public String getRequiredMessage () {
    return "Required";
  }

  
  @Override
  public Character newInstance (String source) {
    return source.charAt(0);
  }

  
  @Override
  public Character primalValue() {
    return ' ';
  }

  
  @Override
  protected void validate (Character value) throws UserEntryException {
    // No further validation required
  }

  
  @Override
  public int getFieldSize() {
    return 1;
  }

  
  @Override
  public Character getFromBuffer (SimpleBuffer b) {
    return (char)getUTF8FromBuffer(b);
  }


  @Override
  public void putToBuffer (SimpleBuffer b, Character v) {
    putUTF8ToBuffer (b, (int)v);  
  }
  
  
  @Override
  public int getBufferSize () {
    return BUFFER_UTF8;
  }
  
  
  @Override
  public String getSQLType() {
    return "CHAR";
  }


  @Override
  public void setStatementFromValue (IPreparedStatement stmt, Character value) {
    stmt.setString(String.valueOf(value));
  }


  @Override
  public Character getResultValue (IResultSet resultSet) {
    String s = resultSet.getString();
    if (s.length() == 0) {
      return ' ';
    } else {
      return s.charAt(0);
    }
  }


}
