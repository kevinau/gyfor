package org.gyfor.web.form;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.template.ScopeChain;

import io.undertow.websockets.core.WebSocketChannel;


public class TemplateModelListener implements EntityCreationListener, ItemEventListener, EffectiveEntryModeListener, ContainerChangeListener {

  private final Logger logger = LoggerFactory.getLogger(TemplateModelListener.class);
  
 
  private final ClientDomEdit clientDom;
  private final TemplateHtmlBuilder htmlBuilder;
  private final boolean hasTitle;
  
  private final ProjectionNode rootProjection = new ProjectionNode();
  
  
  TemplateModelListener (WebSocketChannel channel, ITemplateEngine templateEngine, boolean hasTitle) {
    if (channel == null) {
      throw new NullPointerException("channel");
    }
    this.clientDom = new ClientDomEdit(channel);
    this.htmlBuilder = new TemplateHtmlBuilder(templateEngine);
    this.hasTitle = hasTitle;
  }
  
  
  @Override
  public void childAdded(IContainerModel parent, INodeModel node) {
    logger.info("Child {} added within parent {}", node.getName(), parent.getName());
    int index = 32767;
    if (rootProjection.hasChildren()) {
      System.out.println("....... root projection " + rootProjection.hasChildren());
      System.out.println("....... root projection " + rootProjection.getChildren().size());
      String qname = node.getQName();
      System.out.println("....... root projection " + qname);
      boolean found = false;
      for (ProjectionNode child : rootProjection.getChildren()) {
        Matcher matcher = child.getMatcher(qname);
        System.out.println("....... root projection 2 " + qname + " " + child.toString());
        if (matcher.matches()) {
          if (child.omittable()) {
            // This model node matches a projection node that we want to omit.
            return;
          } else {
            // We want this model node.
            index = child.index();
            found = true;
            break;
          }
        }
      }
      if (!found) {
        // No match found, so we don't want this model node.
        return;
      }
    }
    StringWriter writer = new StringWriter();
    htmlBuilder.buildHtml(writer, node, null);
    clientDom.syncChildren("#contentHere" + parent.getNodeId(), "#node" + node.getNodeId(), index, writer.toString());
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
    Map<String, Object> initialContext = new HashMap<>();
    initialContext.put("projection", rootProjection);
    System.out.println("==================== " + entityModel.getName());
    
    initialContext.put(entityModel.getName(), entityModel);
    ScopeChain scopeChain = htmlBuilder.buildHtml(writer, entityModel, initialContext);
    
    // Debug TODO
    System.out.println("=========================================================");
    rootProjection.dump(0);
    System.out.println("=========================================================");

    if (hasTitle) {
      String title = (String)scopeChain.get("title");
      if (title == null) {
        EntityLabelGroup labelGroup = entityModel.getPlan().getLabels();
        title = labelGroup.getTitle();
      }
      clientDom.setTitle(title);
    }

    clientDom.replaceChildren("#contentHere0", writer.toString());     
  }


  @Override
  public void entityDestoryed(IEntityModel node) {
    clientDom.removeChildren("#contentHere0");
  }

}
