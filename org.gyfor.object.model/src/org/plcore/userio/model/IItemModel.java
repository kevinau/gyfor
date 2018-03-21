package org.plcore.userio.model;

import org.plcore.type.IType;
import org.plcore.type.UserEntryException;
import org.plcore.type.UserEntryException.Type;
import org.plcore.userio.INode;

public interface IItemModel extends INodeModel, INode {

  public void setValue (Object value);

  public void setDefaultValue (Object value);
  
  public <T> T getDefaultValue();
  
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
