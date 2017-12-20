package org.gyfor.classifier;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;

public class ClassSet {

  private List<String> classValues = new ArrayList<>();
  
  
  public void add(String classValue) {
    if (!classValues.contains(classValue)) {
      classValues.add(classValue);
    }
  }
  
  
  public Attribute buildClassAttribute() {
    List<String> classAttributeVector = new ArrayList<>();
    for (String value : classValues) {
      classAttributeVector.add(value);
    }
    classAttributeVector.add("?");
    return new Attribute("class", classAttributeVector);
  }
  
  
  public int indexOf(String classValue) {
    return classValues.indexOf(classValue);
  }
  
}
