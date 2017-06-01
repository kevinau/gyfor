package org.gyfor.web.form;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gyfor.http.CallbackAccessor;
import org.gyfor.http.Context;
import org.gyfor.http.Resource;
import org.gyfor.object.model.ContainerChangeListener;
import org.gyfor.object.model.IContainerModel;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.gyfor.object.model.INodeModel;
import org.gyfor.object.plan.IEntityPlan;
import org.gyfor.object.plan.IPlanFactory;
import org.gyfor.template.ITemplateEngine;
import org.gyfor.template.ITemplateEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.ChannelListener;

import com.mitchellbosecke.pebble.tokenParser.TokenParser;

import io.undertow.server.HttpHandler;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;


@Context("/eex")
@Resource(path = "/resources", location = "resources")
@Component(service = HttpHandler.class)
public class ModelWebSocketHandler extends WebSocketProtocolHandshakeHandler {

  private Logger logger = LoggerFactory.getLogger(ModelWebSocketHandler.class);
  
  @Reference
  private ITemplateEngineFactory templateEngineFactory;

  @Reference
  private IPlanFactory planFactory;
    
  @Reference
  private IModelFactory modelFactory;
    
  
  public ModelWebSocketHandler() {
    super (new ModelWebSocketConnectionCallback());
  }
  
  
  private static class ModelWebSocketConnectionCallback implements WebSocketConnectionCallback {

    private Logger logger = LoggerFactory.getLogger(ModelWebSocketConnectionCallback.class);
    
    private ITemplateEngine templateEngine;

    private IPlanFactory planFactory;
    
    private IModelFactory modelFactory;

    private final Map<WebSocketChannel, IEntityModel> sessions = new HashMap<>();
    
    
    public void setup(ITemplateEngine templateEngine, IPlanFactory planFactory, IModelFactory modelFactory) {
      logger.info("setup");
      this.templateEngine = templateEngine;
      this.planFactory = planFactory;
      this.modelFactory = modelFactory;
    }
    
    
    @Override
    public void onConnect(WebSocketHttpExchange exchange, final WebSocketChannel channel) {
      logger.info("onConnect: " + exchange.getRequestURI());
      
      synchronized (sessions) {
        String uri = exchange.getRequestURI();
        int n = uri.lastIndexOf('/');
        String className = uri.substring(n + 1);
        
        IEntityPlan<?> entityPlan = planFactory.getEntityPlan(className);
        IEntityModel entityModel = modelFactory.buildEntityModel(entityPlan);
        entityModel.addContainerChangeListener(new ContainerChangeListener() {
          @Override
          public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> context) {
            Writer writer = new StringWriter();
            NodeChangeMessage.beginMessage(writer, parent.getNodeId(), Action.ADD, node.getNodeId());
            ModelHtmlBuilder.buildHtml(templateEngine, writer, entityModel, context);

            logger.info("Send text: childAdded {}", writer);
            WebSockets.sendText(writer.toString(), channel, null);
          }

          @Override
          public void childRemoved(IContainerModel parent, INodeModel node) {
            Writer writer = new StringWriter();
            NodeChangeMessage.beginMessage(writer, parent.getNodeId(), Action.REMOVE_BY_ID, node.getNodeId());
            //ModelHtmlBuilder.buildHtml(templateEngine, writer, entityModel, context);

            logger.info("Send text: childRemoved {}", writer);
            WebSockets.sendText(writer.toString(), channel, null);
          }
          
        });
//        String id = exchange.getRelativePath();
//        if (id == null || id.length() <= 1) {
//          HttpUtility.endWithStatus(exchange, 400, "Document id not specified as part of request");
//          return;
//        }
//        // Remove leading slash (/)
//        id = id.substring(1);

        sessions.put(channel, entityModel);
        channel.getCloseSetter().set(new ChannelListener<Channel>() {

          @Override
          public void handleEvent(Channel channel) {
            synchronized (sessions) {
              sessions.remove(channel);
            }
          }
        });
        
      }
      
      channel.getReceiveSetter().set(new AbstractReceiveListener() {

        @Override
        protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
          String data = message.getData();
          
          logger.info("onFullTextMessge: " + data);
          
          int n = data.indexOf('|');
          String command;
          if (n == -1) {
            command = data;
          } else {
            command = data.substring(0,  n);
            data = data.substring(n + 1);
          }
          switch (command) {
          case "close" :
            synchronized (sessions) {
              sessions.remove(channel);
              try {
                channel.close();
              } catch (IOException ex) {
                throw new RuntimeException(ex);
              }
            }
            break;
          case "new" :
            synchronized (sessions) {
              IEntityModel entityModel = sessions.get(channel);
              
              Map<String, Object> context = new HashMap<>();
//              String className = entityModel.getCanonicalName();
//              context.put("entityName", className);
//              context.put(className, entityModel);
              context.put("fieldSet", new LinkedHashMap<String, Map<String, Object>>());
              
              Writer writer = new StringWriter();
              NodeChangeMessage.beginMessage(writer, 0, Action.ADD, entityModel.getNodeId());
              ModelHtmlBuilder.buildHtml(templateEngine, writer, entityModel, context);

              logger.info("Send text: {}", writer);
//              @SuppressWarnings("unchecked")
//              List<String> fieldSet = (List<String>)context.get("fieldSet");
//              logger.info("Field list size: {}", fieldList.size());
//              for (String s : fieldList) {
//                logger.info("... " + s);
//              }
              WebSockets.sendText(writer.toString(), channel, null);

              // Create and set an empty instance of the class
              Object instance = entityModel.newInstance();
              entityModel.setValue(instance);
            }
            break;
          default :
            logger.error("Command not recognised: {}", command);
            break;
          }
          ////WebSockets.sendText(message.getData(), channel, null);
        }
      });
  
      channel.resumeReceives();
    }
    
//    private Timer timer;
//    
//    
//    public void startTicking () {
//      TimerTask updateTask = new TimerTask () {
//
//        @Override
//        public void run() {
//          synchronized (sessions) {
//            i++;
//            System.out.println("........ ticking " + i);
//            for (WebSocketChannel session : sessions) {
//              System.out.println(".......... sending to " + session);
//              WebSockets.sendText("add|v" + i + "|Label " + i, session, null);
//            }
//          }
//        }
//        
//      };
//      timer = new Timer();
//      timer.schedule(updateTask,
//                     0,          //initial delay
//                     5 * 1000);  //subsequent rate
//    }
//    
//    
//    public void stopTicking () {
//      timer.cancel();
//    }
  
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    logger.info("Activating");

    ITemplateEngine templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
    
    TokenParser entityTokenParser = new EntityTokenParser();
    templateEngine.addTokenParser(entityTokenParser);
    
    TokenParser fieldTokenParser = new FieldTokenParser();
    templateEngine.addTokenParser(fieldTokenParser);
    
    ModelWebSocketConnectionCallback callback = CallbackAccessor.getCallback(this);
    callback.setup(templateEngine, planFactory, modelFactory);
  }
  
  
  @Deactivate 
  public void deactivate () {
  }
  
}
