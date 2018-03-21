package org.plcore.sql;

public interface IEntityResultSet<T> extends IResultSet {

  public T getEntity();

}