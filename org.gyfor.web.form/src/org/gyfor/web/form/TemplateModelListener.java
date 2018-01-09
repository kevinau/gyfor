package org.gyfor.web.form;

import java.io.StringWriter;
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
import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.template.ITemplateEngine;

import com.mitchellbosecke.pebble.template.ScopeChain;

import io.undertow.websockets.core.WebSocketChannel;


public class TemplateModelListener implements EntityCreationListener, ItemEventListener, EffectiveEntryModeListener, ContainerChangeListener {

  private final ClientDomEdit clientDom;
  private final TemplateHtmlBuilder htmlBuilder;
  
  TemplateModelListener (WebSocketChannel channel, ITemplateEngine templateEngine) {
    if (channel == null) {
      throw new NullPointerException("channel");
    }
    this.clientDom = new ClientDomEdit(channel);
    this.htmlBuilder = new TemplateHtmlBuilder(templateEngine);
  }
  
  
  @Override
  public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> context) {
    // Context is not used here
    StringWriter writer = new StringWriter();
    htmlBuilder.buildHtml(writer, node, null);
    clientDom.addChildren("#contentHere" + parent.getNodeId(), writer.toString());
  }


  @Override
  public void childRemoved(IContainerModel parent, INodeModel node) {
    clientDom.removeNode(node.getNodeId());
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
    clientDom.clearError(node.getNodeId());
  }

  
  @Override
  public void errorNoted(INodeModel node, UserEntryException ex) {
    clientDom.noteError(node.getNodeId(), ex);
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
    
    // TODO the null in the following should be context from the template
    StringWriter writer = new StringWriter();
    ScopeChain scopeChain = htmlBuilder.buildHtml(writer, entityModel, null);

    String title = (String)scopeChain.get("title");
    if (title == null) {
      EntityLabelGroup labelGroup = entityModel.getPlan().getLabels();
      title = labelGroup.getTitle();
    }
    clientDom.setTitle(title);

    clientDom.replaceChildren("#contentHere0", writer.toString());      
  }


  @Override
  public void entityDestoryed(IEntityModel node) {
    clientDom.removeChildren("#contentHere0");
  }

}
