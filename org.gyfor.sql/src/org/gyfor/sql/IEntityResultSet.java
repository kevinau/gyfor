package org.gyfor.sql;

public interface IEntityResultSet<T> extends IResultSet {

  public T getEntity();

}