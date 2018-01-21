package org.gyfor.web.form;

import org.gyfor.http.WebSocketSession;
import org.gyfor.object.UserEntryException;

import io.undertow.websockets.core.WebSocketChannel;


public class ClientDomEdit {

  private final WebSocketChannel channel;

  public ClientDomEdit(WebSocketChannel channel) {
    this.channel = channel;
  }

  /**
   * Add the HTML to a node identified by selector. Selector should identify
   * exactly one node. The HTML source is typically one or more child nodes.
   */
  public void addChildren(String selector, String htmlSource) {
    WebSocketSession.send(channel, "addChildren", selector, htmlSource);
  }

  /**
   * Add the HTML to a node identified by the id. The HTML source is typically
   * one or more child nodes.
   */
  public void addChildren(int id, String htmlSource) {
    WebSocketSession.send(channel, "addChildren", "#node" + id, htmlSource);
  }

  public void syncChildren(String parentSelector, String nodeSelector, int index, String htmlSource) {
    WebSocketSession.send(channel, "syncChildren", parentSelector, nodeSelector, index, htmlSource);
  }

  public void replaceChildren(String selector, String htmlSource) {
    WebSocketSession.send(channel, "replaceChildren", selector, htmlSource);
  }

  public void replaceChildren(int id, String htmlSource) {
    WebSocketSession.send(channel, "replaceChildren", "#node" + id, htmlSource);
  }

  public void replaceNode(String selector, String htmlSource) {
    WebSocketSession.send(channel, "replaceNode", selector, htmlSource);
  }

  /**
   * Remove all the children of the nodes that are matched by the selector. The nodes
   * matched by the selector are not removed, but their children are.
   */
  public void removeChildren(String selector) {
    WebSocketSession.send(channel, "removeChildren", selector);
  }

  /**
   * Remove all the children of the node identified by the id. The node
   * identified by id is not removed.
   */
  public void removeChildren(int id) {
    WebSocketSession.send(channel, "removeChildren", "#node" + id);
  }

  /**
   * Remove the node that is identified by the id.
   */
  public void removeNode(int id) {
    WebSocketSession.send(channel, "removeNodes", "#node" + id);
  }

  /**
   * Remove all nodes that match the selector.
   */
  public void removeNodes(String selector) {
    WebSocketSession.send(channel, "removeNodes", selector);
  }
  
  public void noteError(int id, UserEntryException ex) {
    WebSocketSession.send(channel, "noteError", id, ex.getType(), ex.getMessage(), ex.getCompletion());
  }
  
  public void clearError(int id) {
    WebSocketSession.send(channel, "clearError", id);
  }
  
  /**
   * Toggle a class name on the nodes that match the selector.
   */
  public void toggleClassName(String selector, String name) {
    WebSocketSession.send(channel, "toggleClass", name);
  }
  
  /**
   * Replace a class name on the nodes that match the selector.
   */
  public void toggleClassName(String selector, String oldName, String newName) {
    WebSocketSession.send(channel, "replaceClass", oldName, newName);
  }
  
  
  public void setTitle(String title) {
    WebSocketSession.send(channel, "setTitle", title);
  }
  
}
