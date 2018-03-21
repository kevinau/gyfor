package org.plcore.userio.plan.impl;

import java.lang.reflect.Array;
import java.util.Iterator;

import org.gyfor.util.ArrayIterator;
import org.plcore.userio.EntryMode;
import org.plcore.userio.plan.IArrayPlan;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.PlanFactory;
import org.plcore.userio.plan.PlanStructure;


public class ArrayPlan extends RepeatingPlan implements IArrayPlan {

  public ArrayPlan (PlanFactory planFactory, MemberValueGetterSetter field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
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
    Object[] instance = (Object[])Array.newInstance(getElementClass(), 0);
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
