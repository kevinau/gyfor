package org.gyfor.object.type.builtin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.gyfor.object.type.ICaseSettable;
import org.gyfor.object.type.ILengthSettable;
import org.gyfor.object.type.Position;
import org.gyfor.object.TextCase;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.type.builtin.Type;


public abstract class StringBasedType<T> extends Type<T> implements ILengthSettable, ICaseSettable {

  private int maxLength = 255;
  private TextCase allowedCase = TextCase.MIXED;

  
  public StringBasedType() {
  }

  public StringBasedType(int maxLength) {
    this.maxLength = maxLength;
  }

  public StringBasedType(int maxLength, TextCase allowedCase) {
    this.maxLength = maxLength;
    this.allowedCase = allowedCase;
  }


  @Override
  public T createFromString(String source) throws UserEntryException {
    int n = source.length();
    if (n > maxLength) {
      String msg = MessageFormat.format("more than {0} characters", Integer.toString(maxLength));
      throw new UserEntryException(msg);
    }
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
    return createFromString2(source);
  }

  
  @Override
  public void setAllowedCase(TextCase allowedCase) {
    this.allowedCase = allowedCase;
  }

  
  @Override
  public void setMaxLength (int maxLength) {
    this.maxLength = maxLength;
  }

  
  @Override
  public String getRequiredMessage () {
    return "must not be blank";
  }

  
  @Override
  public int getFieldSize () {
    return maxLength;
  }
  
//  protected void validateString(String source, boolean creating) throws UserEntryException {
//    int n = source.length();
//    if (n > getMaxSize()) {
//      String msg = MessageFormat.format("more than {0} characters", Integer.toString(getMaxSize()));
//      throw new UserEntryException(msg);
//    } else if (n > 0 && source.charAt(0) == ' ') {
//      if (source.charAt(n - 1) == ' ') {
//        throw new UserEntryException("cannot start or end with a space");
//      } else {
//        throw new UserEntryException("cannot start with a space");
//      }
//    } else if (n > 0 && source.charAt(n - 1) == ' ') {
//      throw new UserEntryException("cannot end with a space", true);
//    }
//  }

  
  @Override
  public Object getFromBuffer(byte[] data, Position p) {
    return getStringFromBuffer(data, p);
  }

  
  @Override
  public String getSQLType() {
    return "VARCHAR(" + getFieldSize() + ")";
  }

  
  protected T createFromString2(String source) throws UserEntryException {
    T value = newInstance(source);
    validate (value);
    return value;
  }

  
  @Override
  public abstract T primalValue();


  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, T value) throws SQLException {
    stmt.setString(sqlIndex, value.toString());
    
  }


  @SuppressWarnings("unchecked")
  @Override
  public T getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    return (T)resultSet.getString(sqlIndex);
  }

}  
