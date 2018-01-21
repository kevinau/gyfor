package org.gyfor.web.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectionNode {
  
  private final Pattern nodePattern;
  
  private final Map<String, Object> withValues;
  
  private final boolean omit;

  private final List<ProjectionNode> children = new ArrayList<>();
  
  private int index = 0;
  
  
  public ProjectionNode() {
    this.nodePattern = Pattern.compile(".*");
    this.withValues = null;
    this.omit = false;
  }
  
  
  public ProjectionNode(String simpleNodePath, Map<String, Object> withValues) {
    this.nodePattern = Pattern.compile(simpleNodePath);
    this.withValues = withValues;
    this.omit = false;
  }
  
  
  public ProjectionNode(String simpleNodePath, boolean omit) {
    this.nodePattern = Pattern.compile(simpleNodePath);
    this.withValues = null;
    this.omit = omit;
  }
  
  
  public void add(ProjectionNode pnode) {
    pnode.index = children.size();
    children.add(pnode);
  }
  
  
  public Matcher getMatcher(String arg) {
    return nodePattern.matcher(arg);
  }
  
  
  public Map<String, Object> getWithValues() {
    return withValues;
  }
  
  
  public boolean hasChildren() {
    return children.size() > 0;
  }
  
  
  public List<ProjectionNode> getChildren() {
    return children;
  }
  
  
  public boolean omittable() {
    return omit;
  }
  
  
  public int index() {
    return index;
  }
  
  
  @Override
  public String toString() {
    return "ProjectionNode[" + nodePattern.pattern() + "," + withValues + "," + omit + ",#" + children.size() + "]";
  }
  
  
  public void dump (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
    System.out.println(nodePattern + ": " + withValues);
    for (ProjectionNode child : children) {
      child.dump(level + 1);
    }
  }
}
