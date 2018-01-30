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
import org.gyfor.object.model.IItemModel;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.model.ItemEventListener;
import org.gyfor.object.plan.EntityLabelGroup;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.web.form.state.OptionChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mitchellbosecke.pebble.template.ScopeChain;

import io.undertow.websockets.core.WebSocketChannel;


public class TemplateModelListener implements EntityCreationListener, ItemEventListener, 
                                              EffectiveEntryModeListener, ContainerChangeListener,
                                              OptionChangeListener {

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
    int index = projectionIndex(node);
    if (index != -1) {
      StringWriter writer = new StringWriter();
      htmlBuilder.buildHtml(writer, node, null);
      clientDom.syncChildren("#contentHere" + parent.getNodeId(), "#node" + node.getNodeId(), index, writer.toString());
    }
  }


  @Override
  public void childRemoved(IContainerModel parent, INodeModel node) {
    clientDom.removeNode(node.getNodeId());
  }

  
  private int projectionIndex(INodeModel node) {
    if (rootProjection.hasChildren()) {
      String qname = node.getQName();
      
      boolean found = false;
      for (ProjectionNode child : rootProjection.getChildren()) {
        Matcher matcher = child.getMatcher(qname);
        if (matcher.matches()) {
          if (child.omittable()) {
            // This model node matches a projection node that we want to omit.
            return -1;
          } else {
            // We want this model node.
            return child.index();
          }
        }
      }
      if (!found) {
        // No match found, so we don't want this model node.
        return -1;
      }
    }
    // Return a large number (but not too large that it upsets JavaScript)
    return 32767;
  }
  
  
  @Override
  public void effectiveModeChanged(INodeModel node, EffectiveEntryMode priorMode) {
    logger.info("Effective mode changed: {} to {}", node.getName(), node.getEffectiveEntryMode());
    
    int index = projectionIndex(node);
    if (index != -1) {
      clientDom.setEntryMode(node.getNodeId(), node.getEffectiveEntryMode());
    }
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
    logger.info("Value change: name {} = value '{}'", node.getName(), ((IItemModel)node).getValueAsSource());
    // I don't think we need to respond to this event.
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
    logger.info("Source change: name {} = value '{}'", node.getName(), ((IItemModel)node).getValueAsSource());
    if (!(node instanceof IItemModel)) {
      throw new IllegalArgumentException("Expecting IItemModel, but was given " + node.getClass());
    }
    int index = projectionIndex(node);
    logger.info("index " + index);
    if (index != -1) {
      IItemModel inode = (IItemModel)node;
      clientDom.setValue(inode.getNodeId(), inode.getValueAsSource());
    }
  }

  @Override
  public void comparisonBasisChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void entityCreated(IEntityModel node) {
    IEntityModel entityModel = (IEntityModel)node;
    
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


  @Override
  public void optionChanged(Enum<?> option, boolean available) {
    clientDom.changeOption(option, available);
  }

}
