package org.gyfor.web.form;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.template.ITemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;


public class ProxyWebSocketConnectionCallback implements WebSocketConnectionCallback {

  private Logger logger = LoggerFactory.getLogger(ProxyWebSocketConnectionCallback.class);

  private ITemplateEngine templateEngine;

  private IPlanFactory planFactory;

  private IModelFactory modelFactory;
  

  public void setup(ITemplateEngine templateEngine, IPlanFactory planFactory, IModelFactory modelFactory) {
    // defaultContext, templateEngine and objectContext are shared amongst all
    // ProxyWebSocketConnectionCallback
    this.templateEngine = templateEngine;
    this.planFactory = planFactory;
    this.modelFactory = modelFactory;
  }


  @Override
  public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
    if (templateEngine == null) {
      throw new IllegalStateException("Web socket 'onConnect' before this callback has been setup");
    }
    channel.getReceiveSetter().set(new AbstractReceiveListener() {

      @Override
      protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
        String data = message.getData();
        logger.info("On full text message: {}", data);

        // Following GET commands are supported:
        // <action><fully qualified class name>?<id>
        // where action is a single character
        switch (data.charAt(0)) {
        case 'N' :
          // ModelChangeListener structureChangeListener = new
          // ModelChangeListener() {
          // @Override
          // public void childAdded(NodeModel parent, NodeModel node) {
          // String className = data.substring(1);
          //
          // Writer writer = new StringWriter();
          // buildEntityHtml(writer, className, node);
          //
          // //int nodeId = node.getId();
          // //String html = "<p id='" + nodeId + "'>Child added</p>";
          // NodeChangeMessage addMessage = new
          // NodeChangeMessage(parent.getId(), Action.ADD, writer.toString());
          // WebSockets.sendText(addMessage.toString(), channel, null);
          // }
          //
          //
          // @Override
          // public void childRemoved(NodeModel parent, NodeModel node) {
          // NodeChangeMessage addMessage = new
          // NodeChangeMessage(parent.getId(), Action.REMOVE_BY_ID,
          // node.getId());
          // WebSockets.sendText(addMessage.toString(), channel, null);
          // }
          // };
          // formModel.addStructureChangeListener(structureChangeListener);

          String className = data.substring(1);

          // Build an entityModel for the class
          IEntityPlan<?> entityPlan = planFactory.getEntityPlan(className);
          IEntityModel entityModel = modelFactory.buildEntityModel(entityPlan);
          
          // Create and set an empty instance of the class
          Object instance = entityPlan.newInstance();
          entityModel.setValue(instance);
          
          Map<String, Object> context = new HashMap<>();
          context.put("entityName", className);
          context.put(className, entityModel);
          
          Writer writer = new StringWriter();
          NodeChangeMessage.beginMessage(writer, 0, Action.ADD, entityModel.getNodeId());
          ModelHtmlBuilder.buildHtml(templateEngine, writer, entityModel, context);

          logger.info("Send text: {}", writer);
          WebSockets.sendText(writer.toString(), channel, null);
          break;
        case 'A' :
          int id = 12345;
          String html = "<div id='node-" + id + "'>Node " + id + "</div>";
          NodeChangeMessage addMessage = new NodeChangeMessage(0, Action.ADD, 0, html);
          logger.info(">>>>>>>>>>>>>>>>>>>Send text: " + addMessage.toString());
          WebSockets.sendText(addMessage.toString(), channel, null);
          break;
        case 'R' :
          int n1 = Integer.parseInt(data.substring(1));
          NodeChangeMessage removeMessage1 = new NodeChangeMessage(0, Action.REMOVE_BY_ID, n1);
          logger.info(">>>>>>>>>>>>>>>>>>>Send text: " + removeMessage1.toString());
          WebSockets.sendText(removeMessage1.toString(), channel, null);
          break;
        case 'r' :
          int n2 = Integer.parseInt(data.substring(1));
          NodeChangeMessage removeMessage2 = new NodeChangeMessage(0, Action.REMOVE_BY_INDEX, n2);
          logger.info(">>>>>>>>>>>>>>>>>>>Send text: " + removeMessage2.toString());
          WebSockets.sendText(removeMessage2.toString(), channel, null);
          break;
        case '!' :
          // This is an acknowledgment from the browser
          logger.info(">>>>>>>>>>>>>>>>>>>Acknowledgement received " + data);
          break;
        default :
          throw new RuntimeException("Unsupported command: " + data);
        }
      }
    });
    channel.resumeReceives();
  }


//  public void returnHtml(int containerId, INodePlan nodePlan, int id, Object value, WebSocketChannel channel) {
//    if (nodePlan instanceof IEntityPlan) {
//      IEntityPlan<?> entityPlan = (IEntityPlan<?>)nodePlan;
//      String className = entityPlan.getClassName();
//      Template entityTemplate = templateEngine.getTemplate(className);
//
//      List<QueuedField> queuedFields = new ArrayList<>();
//
//      Map<String, Object> templateContext = new HashMap<>();
//      templateContext.put("rootModel", rootModel);
//      templateContext.put("queuedFields", queuedFields);
//      templateContext.put("entityName", className);
//      templateContext.put("id", id);
//      templateContext.put("labels", entityPlan.getLabels());
//
//      Writer writer = new StringWriter();
//      entityTemplate.evaluate(writer, templateContext);
//      NodeChangeMessage addMessage = new NodeChangeMessage(0, true, Action.ADD, id, writer.toString());
//      logger.info("Send text: " + addMessage.toString());
//      WebSockets.sendText(addMessage.toString(), channel, null);
//
//      // Wait until we get an acknowledgement (sync) from the browser before continuing.  The semaphore
//      // has already been acquired so this will hang until released by the sync action.
//      System.out.println("About to acquire a semaphore: " + semaphore.availablePermits());
//      try {
//        semaphore.acquire();
//      } catch (InterruptedException ex) {
//        throw new RuntimeException(ex);
//      }
//      
//      int i = 0;
//      for (QueuedField qf : queuedFields) {
//        String memberName = qf.getName();
//        System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] " + memberName);
//        for (INodePlan p : entityPlan.getMemberPlans()) {
//          System.out.println("]]]]]]]]]]]]]]]]]]]] " + p);
//        }
//        INodePlan memberPlan = entityPlan.getMemberPlan(memberName);
//        returnHtml(id, memberPlan, qf.getId(), value, channel);
//        i++;
//      }
//    } else if (nodePlan instanceof IItemPlan) {
//      IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
//      IType<?> itemType = itemPlan.getType();
//
//      Template itemTemplate = templateEngine.getTemplate(itemType.getClass().getSimpleName());
//
//      Map<String, Object> templateContext = new HashMap<>();
//      templateContext.put("id", id);
//      templateContext.put("labels", itemPlan.getLabels());
//
//      Writer writer = new StringWriter();
//      itemTemplate.evaluate(writer, templateContext);
//      NodeChangeMessage addMessage = new NodeChangeMessage(containerId, false, Action.ADD, id, writer.toString());
//      logger.info(".>>>>>>>>>>>>Send text: " + addMessage.toString());
//      WebSockets.sendText(addMessage.toString(), channel, null);
//    } else {
//      throw new RuntimeException("Unsupported node plan: " + nodePlan);
//    }
//  }


//  private void buildEntityHtml(Writer writer, String entityName, NodeModel nodeModel) {
//    // Can we use an existing template?
//    String typeName = nodeModel.getClass().getName();
//
//    Template template = templateEngine.getTemplate(typeName);
//
//    //// EntityLabels labelGroup = EntityLabels.extract(entityName);
//    Map<String, Object> entityContext = new HashMap<>();
//    entityContext.put("id", nodeModel.getId());
//    //// entityContext.put("label", nodeModel.getPlan().getLabels());
//    template.evaluate(writer, entityContext);
//  }

  // private void buildEntityHtml(Writer writer, String entityName, int id) {
  // // Can we use an existing template?
  // PebbleTemplate template = templateCache.get(entityName);
  // if (template != null) {
  // // Yes. We're good to go.
  // } else {
  // // No. So is there a custom template for this entity?
  // String templateName = entityName;
  // if (loader.exists(templateName)) {
  // // A custom template exists, so compile it and add it to the cache.
  // template = getEntityTemplate(templateName);
  // templateCache.put(entityName, template);
  // } else {
  // // Otherwise, use the standard standardEntity template.
  // template = getDefaultEntityTemplate();
  // }
  //
  // EntityLabelGroup labelGroup = EntityLabelGroup.extract(entityName);
  // Map<String, Object> entityContext = new HashMap<>();
  // entityContext.put("id", id);
  // entityContext.put("shortTitle", labelGroup.getShortTitle());
  // entityContext.put("title", labelGroup.getTitle());
  // entityContext.put("description", labelGroup.getDescription());
  // try {
  // template.evaluate(writer, entityContext);
  // } catch (PebbleException | IOException ex) {
  // throw new RuntimeException(ex);
  // }
  // }
  // }
  //
}
