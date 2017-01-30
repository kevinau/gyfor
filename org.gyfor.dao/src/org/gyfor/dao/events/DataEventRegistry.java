package org.gyfor.dao.events;

import org.gyfor.dao.DataChangeListener;
import org.gyfor.dao.IDataEventRegistry;
import org.gyfor.util.FastAccessList;
import org.osgi.service.component.annotations.Component;


@Component(immediate=true)
public class DataEventRegistry implements IDataEventRegistry {

  private final FastAccessList<DataChangeListener> dataChangeListenerList = new FastAccessList<>(DataChangeListener.class);

  
 @Override
  public void addDataChangeListener(DataChangeListener x) {
    dataChangeListenerList.add(x);
  }

  @Override
  public void removeDataChangeListener(DataChangeListener x) {
    dataChangeListenerList.remove(x);
  }

  @Override
  public void fireEntityAdded(Object entity) {
    for (DataChangeListener listener : dataChangeListenerList) {
      listener.entityAdded(entity);
    }
  }


  @Override
  public void fireEntityChanged(Object entity) {
    for (DataChangeListener listener : dataChangeListenerList) {
      listener.entityChanged(entity);
    }
  }


  @Override
  public void fireEntityRemoved(Object entity) {
    for (DataChangeListener listener : dataChangeListenerList) {
      listener.entityRemoved(entity);
    }
  }

  @Override
  public void fireEntityRetired(int id) {
    for (DataChangeListener listener : dataChangeListenerList) {
      listener.entityRetired(id);
    }
  }

  @Override
  public void fireEntityUnretired(int id) {
    for (DataChangeListener listener : dataChangeListenerList) {
      listener.entityUnretired(id);
    }
  }

}
