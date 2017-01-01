package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.gyfor.object.EntryMode;
import org.gyfor.object.IPlanFactory;
import org.gyfor.object.plan.PlanStructure;

public class ListPlan extends RepeatingPlan {

  public ListPlan (IPlanFactory context, Field field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
    super (context, field, elemClass, name, entryMode, dimension);
  }
  

  @Override
  public int getElementCount(Object value) {
    List<?> listValue = (List<?>)value;
    return listValue.size();
  }


  @Override
  public Object getElementValue(Object value, int i) {
    List<?> listValue = (List<?>)value;
    return listValue.get(i);
  }

  
  @SuppressWarnings("unchecked")
  @Override 
  public <X> Iterator<X> getIterator (Object instance) {
    List<X> list = (List<X>)instance;
    return list.iterator();
  }

    
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.LIST;
  }

}
