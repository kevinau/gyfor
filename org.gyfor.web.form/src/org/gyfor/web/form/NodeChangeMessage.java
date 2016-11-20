package org.gyfor.web.form;

import java.io.IOException;
import java.io.Writer;

public class NodeChangeMessage {

  private final int containerId;
  private final Action action;
  private final int nodeId;
  private final String html;
  
  
  public NodeChangeMessage (int containerId, Action action, int nodeId, String html) {
    this.containerId = containerId;
    this.action = action;
    this.nodeId = nodeId;
    this.html = html;
  }
  
  public NodeChangeMessage (int containerId, Action action, int nodeId) {
    this (containerId, action, nodeId, "");
  }

  
  private static final char US = '|';
  
  
  @Override
  public String toString() {
    StringBuilder msg = new StringBuilder();
    msg.append(containerId);
    msg.append(US);
    msg.append(action.toCode());
    msg.append(US);
    msg.append(nodeId);
    msg.append(US);
    msg.append(html);
    return msg.toString();
  }

  
  public static void beginMessage(Writer writer, int containerId, Action action, int nodeId) {
    StringBuilder msg = new StringBuilder();
    msg.append(containerId);
    msg.append(US);
    msg.append(action.toCode());
    msg.append(US);
    msg.append(nodeId);
    msg.append(US);
    try {
      writer.append(msg.toString());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
}
