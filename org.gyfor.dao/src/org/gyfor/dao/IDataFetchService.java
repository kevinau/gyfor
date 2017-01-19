package org.gyfor.dao;

import java.util.List;

public interface IDataFetchService {

  public <T> T getById (int id);
  
  public <T> T getByKey (Object... keyValues);
  
  public boolean existsByKey (Object... keyValues);
  
  public <T> List<T> getAll ();
  
  public List<IdValuePair<String>> getDescriptionAll ();
  
}
