package org.gyfor.web.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectionNode {
  
  private final String fieldName;
  
  private final Map<String, Object> context;

  private final List<ProjectionNode> children = new ArrayList<>();
  
  
  public ProjectionNode() {
    this.fieldName = "ROOT";
    this.context = null;
  }
  
  
  public ProjectionNode(String fieldName, Map<String, Object> context) {
    this.fieldName = fieldName;
    this.context = context;
  }
  
  
  public void add(ProjectionNode pnode) {
    children.add(pnode);
  }
  
  
  public String getFieldName() {
    return fieldName;
  }
  
  
  public Map<String, Object> getContext() {
    return context;
  }
  
  
  @Override
  public String toString() {
    return "ProjectionNode[" + fieldName + "," + context + ",#" + children.size() + "]";
  }
  
  
  public void dump (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
    System.out.println(fieldName + ": " + context);
    for (ProjectionNode child : children) {
      child.dump(level + 1);
    }
  }
}
