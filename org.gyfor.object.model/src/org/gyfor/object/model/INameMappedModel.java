package org.gyfor.object.model;

import org.gyfor.object.INameMappedNode;

public interface INameMappedModel extends IContainerModel, INameMappedNode<INodeModel> {

  public <X extends INodeModel> X getMember(String name);

}
