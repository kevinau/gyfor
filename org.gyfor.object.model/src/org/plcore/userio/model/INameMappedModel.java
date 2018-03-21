package org.plcore.userio.model;

import org.plcore.userio.INameMappedNode;

public interface INameMappedModel extends IContainerModel, INameMappedNode {

  public <X extends INodeModel> X getMember(String name);

}
