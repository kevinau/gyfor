package org.gyfor.object.model;

import org.gyfor.object.INode;
import org.gyfor.object.UserEntryException;
import org.gyfor.object.UserEntryException.Type;
import org.gyfor.object.type.IType;

public interface IItemModel extends INodeModel, INode {

  public void setValue (Object value);

  public void setDefaultValue (Object value);
  
  public String toEntryString(Object value);

  public void setComparisonBasis(ComparisonBasis comparisonBasis);

  public boolean isInError();

  public UserEntryException.Type getStatus();
  
  public IType<?> getType();

  public Type getStatus(int order);

  public String getStatusMessage();

  public UserEntryException[] getErrors();

  public void setValueFromSource(String source);
  
  public default boolean isId() {
    return false;
  }
  
  public default boolean isVersion() {
    return false;
  }

  public String getValueAsSource();

}
