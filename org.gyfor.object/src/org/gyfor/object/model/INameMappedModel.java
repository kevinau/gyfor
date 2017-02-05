package org.gyfor.object.model;

public interface INameMappedModel extends IContainerModel {

  public <X extends INodeModel> X getMember(String name);

}