package org.gyfor.dbloader.berkeley;

public interface DatabaseLoader {

  public void open (Class<?> klass, String[] itemPaths);
  
  public void load (String[] values);
  
  public void close ();
  
}
