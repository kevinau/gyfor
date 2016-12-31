package org.gyfor.object.plan;

import java.util.Iterator;

public interface IRepeatingPlan extends INodePlan {

  public int getDimension();
  
  public INodePlan getElementPlan ();

  public int getMaxOccurs();

  public int getMinOccurs();
  
  public int getElementCount(Object value);

  public Object getElementValue(Object value, int i);

  public <X> Iterator<X> getIterator(Object value);

}
