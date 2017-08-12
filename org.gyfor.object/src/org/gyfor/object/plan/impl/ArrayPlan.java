package org.gyfor.object.plan.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Iterator;

import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.IArrayPlan;
import org.gyfor.object.plan.PlanFactory;
import org.gyfor.object.plan.PlanStructure;
import org.gyfor.util.ArrayIterator;


public class ArrayPlan extends RepeatingPlan implements IArrayPlan {

  public ArrayPlan (PlanFactory planFactory, Field field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
    super (planFactory, field, elemClass, name, entryMode, dimension);
  }
  

  @Override
  public int getElementCount(Object value) {
    Object[] arrayValue = (Object[])value;
    return arrayValue.length;
  }


  @Override
  public Object getElementValue(Object value, int i) {
    Object[] arrayValue = (Object[])value;
    return arrayValue[i];
  }

  
  @Override
  public <X> Iterator<X> getIterator (Object value) {
    return new ArrayIterator<X>(value);
  }
    
  
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.ARRAY;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X> X newInstance() {
    Object[] instance = (Object[])Array.newInstance(getElemClass(), 0);
    return (X)instance;
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public <X> X newInstance(X fromValue) {
    Object[] fromArray = (Object[])fromValue;
    Object[] toArray = (Object[])Array.newInstance(Object.class, fromArray.length);
    for (int i = 0; i < fromArray.length; i++) {
      Object v = super.newInstance(fromArray[i]);
      toArray[i] = v;
    }
    return (X)toArray;
  }

  
  @Override
  public String toString() {
    return "ArrayPlan[" + super.toString() + "]";  
  }
  
}
