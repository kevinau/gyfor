package org.gyfor.web.form;

import org.gyfor.http.WebSocketSession;

import io.undertow.websockets.core.WebSocketChannel;


public class ClientHtmlEdit {

  private final WebSocketChannel channel;

  public ClientHtmlEdit(WebSocketChannel channel) {
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
    WebSocketSession.send(channel, "addChildren", "#" + id, htmlSource);
  }

  public void replaceChildren(String selector, String htmlSource) {
    WebSocketSession.send(channel, "replaceChildren", selector, htmlSource);
  }

  public void replaceChildren(int id, String htmlSource) {
    WebSocketSession.send(channel, "replaceChildren", "#" + id, htmlSource);
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
    WebSocketSession.send(channel, "removeChildren", "#" + id);
  }

  /**
   * Remove the node that is identified by the id.
   */
  public void removeNode(int id) {
    WebSocketSession.send(channel, "removeNodes", "#" + id);
  }

  /**
   * Remove all nodes that match the selector.
   */
  public void removeNodes(String selector) {
    WebSocketSession.send(channel, "removeNodes", selector);
  }

}
