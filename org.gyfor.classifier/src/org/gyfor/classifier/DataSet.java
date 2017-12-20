package org.gyfor.classifier;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class DataSet {

  private final List<String> classValues = new ArrayList<>();
  
  private final Attribute classAttribute;
  
  private final Attribute textAttribute;
  
  public DataSet () {
    List<String> classVector = null; // null -> String type
    classAttribute = new Attribute("class", classVector);
    
    List<String> textVector = null; // null -> String type
    textAttribute = new Attribute("text", textVector);
  }
  
  
  public void addData(String classValue, String text) {
    if (!classValues.contains(classValue)) {
      classValues.add(classValue);
      classAttribute.addStringValue(classValue);
    }

    textAttribute.addStringValue(text);
  }


  public Attribute getClassAttribute() {
    return classAttribute;
  }
  
  
  public Attribute getTextAttribute() {
    return textAttribute;
  }
  
  
  public ArrayList<Attribute> getAttributeInfo() {
    ArrayList<Attribute> attributeInfo = new ArrayList<>(2);
    attributeInfo.add(textAttribute);
    attributeInfo.add(classAttribute);
    return attributeInfo;
  }
  
  
  public Instances populateInstances(String[] inputTexts, String[] inputClasses, Instances theseInstances) {
    for (int i = 0; i < inputTexts.length; i++) {
      Instance inst = newInstance(inputTexts[i], inputClasses[i]);
      theseInstances.add(inst);
    }
    return theseInstances;
  }
  
  
  public Instance newInstance(String inputText, String inputClass) {

    Instance inst = new SparseInstance(2);
    inst.setValue(textAttribute, inputText);
    if (inputClass != null) {
      inst.setValue(classAttribute, inputClass);
    }
    return inst;
  }


}
