package org.gyfor.object.type.builtin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gyfor.object.type.IType;
import org.gyfor.object.type.Position;
import org.gyfor.object.UserEntryException;

public class VoidType implements IType<Void> {

  @Override
  public Void createFromString(String source) throws UserEntryException {
    throw new RuntimeException("There should be no user input for a VoidType field");
  }

  @Override
  public Void createFromString(Void fillValue, boolean nullable, boolean creating, String source) throws UserEntryException {
    throw new RuntimeException("There should be no user input for a VoidType field");
  }

  @Override
  public Void newInstance(String source) {
    return null;
  }

  @Override
  public String toDescriptionString(Void value) {
    return null;
  }

  @Override
  public String toEntryString(Void value, Void fillValue) {
    return "";
  }

  @Override
  public String toValueString(Void value) {
    throw new IllegalStateException("There is not value associated with the Void type");
  }

  @Override
  public Void primalValue() {
    return null;
  }

  @Override
  public void validate(Void value, boolean nullable) throws UserEntryException {
  }

  @Override
  public String getRequiredMessage() {
    return null;
  }

  @Override
  public int getFieldSize() {
    return 0;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  
  @Override
  public Object getFromBuffer(byte[] data, Position p) {
    throw new IllegalStateException("Void type is never retrieved from a database");
  }

  
  @Override
  public String getSQLType() {
    throw new IllegalStateException("Void type is never stored in a database");
  }

  @Override
  public void setSQLValue(PreparedStatement stmt, int sqlIndex, Void value) throws SQLException {
    throw new IllegalStateException("Void type value is never set in the database");
  }

  @Override
  public Void getSQLValue(ResultSet resultSet, int sqlIndex) throws SQLException {
    throw new IllegalStateException("Void type value is never retrieved from the database");
  }

}
