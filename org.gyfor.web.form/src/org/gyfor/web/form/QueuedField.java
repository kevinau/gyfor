package org.gyfor.web.form;


public class QueuedField {

  private final String name;
  
  private final int id;
  
  public QueuedField (String name, int id) {
    this.name = name;
    this.id = id;
  }
  
  public String getName () {
    return name;
  }
  
  public int getId () {
    return id;
  }

  @Override
  public String toString() {
    return "QueuedField [name=" + name + ", id=" + id + "]";
  }
  
}
