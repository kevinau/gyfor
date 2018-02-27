package org.gyfor.object.model.test;

import java.util.ArrayList;
import java.util.List;

public class GenericTest {

  private static interface INode<T> {
    public T getValue();
  }
  
  private static class StringNode implements INode<String> {
    @Override
    public String getValue() {
      return "One";
    }
  }
  
  private static class IntegerNode implements INode<Integer> {
    @Override
    public Integer getValue() {
      return 2;
    }
  }
  
  private static class NodeList<T extends INode<?>> {
    List<T> nodes = new ArrayList<>();
    
    public void add(T node) {
      nodes.add(node);
    }
    
    public List<T> getNodes() {
      return nodes;
    }
    
  }
  
  public static void main (String[] args) {
    NodeList<INode<?>> nodeList = new NodeList<>();
    nodeList.add(new StringNode());
    nodeList.add(new IntegerNode());
    
    for (INode<?> node : nodeList.getNodes()) {
      System.out.println(node.getValue());
    }
  }
}
