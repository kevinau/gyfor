package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.IListPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.plan.PlanStructure;


public class ListPlan extends RepeatingPlan implements IListPlan {

  public ListPlan (PlanFactory planFactory, Field field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
    super (planFactory, field, elemClass, name, entryMode, dimension);
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

  
  @SuppressWarnings("unchecked")
  @Override
  public <X> X newInstance(X fromValue) {
    List<?> fromList = (List<?>)fromValue;
    List<Object> toList = new ArrayList<>(fromList.size());
    for (Object v0 : fromList) {
      Object v1 = super.newInstance(v0);
      toList.add(v1);
    }
    return (X)toList;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X> X newInstance() {
    List<Object> instance = new ArrayList<>();
    return (X)instance;
  }
  
}
