package org.gyfor.web.form;

import java.util.Map;

import org.gyfor.object.UserEntryException;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.EffectiveEntryMode;
import org.gyfor.object.model.EffectiveEntryModeListener;
import org.gyfor.object.model.EntityCreationListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ItemEventListener;
import org.gyfor.template.ITemplateEngine;

import io.undertow.websockets.core.WebSocketChannel;


public class TemplateModelListener implements EntityCreationListener, ItemEventListener, EffectiveEntryModeListener, ContainerChangeListener {

  private final ClientHtmlEdit clientHtml;
  private final TemplateHtmlBuilder htmlBuilder;
  
  TemplateModelListener (WebSocketChannel channel, ITemplateEngine templateEngine) {
    if (channel == null) {
      throw new NullPointerException("channel");
    }
    this.clientHtml = new ClientHtmlEdit(channel);
    this.htmlBuilder = new TemplateHtmlBuilder(templateEngine);
  }
  
  
  @Override
  public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> context) {
    // Context is not used here
    String html = htmlBuilder.buildHtml(node, null);
    clientHtml.addChildren("#contentHere" + parent.getNodeId(), html);
  }


  @Override
  public void childRemoved(IContainerModel parent, INodeModel node) {
    clientHtml.removeNode(node.getNodeId());
  }

  
  @Override
  public void effectiveModeChanged(INodeModel model, EffectiveEntryMode priorMode) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getOrigin() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void valueEqualityChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void sourceEqualityChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void valueChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void errorCleared(INodeModel node) {
    // TODO Auto-generated method stub
    
  }

  
  @Override
  public void errorNoted(INodeModel node, UserEntryException ex) {
    // TODO Auto-generated method stub
    
  }

  
  @Override
  public void sourceChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void comparisonBasisChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void entityCreated(IEntityModel node) {
    IEntityModel entityModel = (IEntityModel)node;
    String html = htmlBuilder.buildHtml(entityModel, null);
    clientHtml.replaceChildren("#contentHere0", html);      
  }


  @Override
  public void entityDestoryed(IEntityModel node) {
    clientHtml.removeChildren("#contentHere0");
  }

}
