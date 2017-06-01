package org.gyfor.object.model;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.UserEntryException.Type;
import org.gyfor.object.type.IType;

public interface IItemModel extends INodeModel {

  public String toEntryString(Object value);

  public void setComparisonBasis(ComparisonBasis comparisonBasis);

  public boolean isInError();

  public Type getStatus();
  
  public IType<?> getType();

  public Type getStatus(int order);

  public String getStatusMessage();

  public UserEntryException[] getErrors();

  public void addItemEventListener(ItemEventListener x);

  public void removeItemEventListener(ItemEventListener x);

  public void setValueFromSource(String source);

}
