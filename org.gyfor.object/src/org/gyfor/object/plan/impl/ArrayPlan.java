package org.gyfor.object.plan.impl;

import java.lang.reflect.Field;
import java.util.Iterator;

import org.gyfor.object.EntryMode;
import org.gyfor.object.plan.IPlanContext;
import org.gyfor.object.plan.PlanStructure;
import org.gyfor.util.ArrayIterator;


public class ArrayPlan extends RepeatingPlan {

  public ArrayPlan (IPlanContext context, Field field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
    super (context, field, elemClass, name, entryMode, dimension);
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
  
}
