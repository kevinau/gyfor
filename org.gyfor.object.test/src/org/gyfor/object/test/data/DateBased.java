package org.gyfor.object.test.data;

import java.time.LocalDate;
import java.util.Date;


public class DateBased {

  public Date date;

  public LocalDate localDate;

  public java.sql.Date sqlDate;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public LocalDate getLocalDate() {
    return localDate;
  }

  public void setLocalDate(LocalDate localDate) {
    this.localDate = localDate;
  }

  public java.sql.Date getSqlDate() {
    return sqlDate;
  }

  public void setSqlDate(java.sql.Date sqlDate) {
    this.sqlDate = sqlDate;
  }

}
