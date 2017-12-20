package org.gyfor.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;


public class TestClassifier4 {

  private String[] inputText = null;
  private String[] inputClasses = null;
  private final String classifierName;

  private Dictionary2 dictionary;
  private ClassSet classSet1;
  
  private Attribute classAttribute = null;
  private ArrayList<Attribute> attributeInfo = null;

  private Instances instances = null;
  private Classifier classifier = null;
  //private Instances filteredData = null;
  private Evaluation evaluation = null;
  //private Set<String> modelWords = null;
  // maybe this should be settable?
  //private String delimitersStringToWordVector = "\\s.,:'\\\"()?!";

  //
  // main, mainly for testing
  //
  public static void main(String args[]) {

    // String classString = "weka.classifiers.bayes.NaiveBayes";
    // String thisClassString = "weka.classifiers.lazy.IBk";
    String classifierName = "weka.classifiers.bayes.NaiveBayes";

    if (args.length > 0) {
      classifierName = args[0];
    }

    String[] inputText = { 
        "hey, buy this from me!", 
        "do you want to buy?", 
        "I have a party tonight!",
        "today it is a nice weather", 
        "you are best", 
        "I have a horse", 
        "you are my friend", 
        "buy, buy, buy!",
        "it is spring in the air", 
        "do you want to come?",
    };

    String[] inputClasses = { 
        "spam", 
        "spam", 
        "no spam", 
        "no spam", 
        "spam", 
        "no spam", 
        "no spam", 
        "spam", 
        "no spam",
        "no spam",
    };

    String[] testText = { 
        "you want to buy from me?", 
        "usually I run in stairs", 
        "buy it now!", 
        "buy, buy, buy!",
        "you are the best, buy!", 
        "it is spring in the air",
    };

    String[] testClasses = { 
        "spam", 
        "no spam", 
        "spam", 
        "spam", 
        "spam",
        "no spam",
    };


    if (inputText.length != inputClasses.length) {
      throw new RuntimeException("The length of text and classes must be the same!");
    }

    // calculate the classValues
//    Set<String> classSet = new HashSet<>(Arrays.asList(inputClasses));
//    classSet.add("?");
//    String[] classValues = (String[])classSet.toArray(new String[0]);

    //
    // create text attribute
    //
//    List<String> inputTextVector = null; // null -> String type
//    Attribute thisTextAttribute = new Attribute("text", inputTextVector);
//    for (int i = 0; i < inputText.length; i++) {
//      thisTextAttribute.addStringValue(inputText[i]);
//    }

    // add test cases (to be inserted into instances)
    // just a singular test string
    /*
     * String newTextString = newTestTextField.getText(); String[] newTextArray
     * = new String[1]; newTextArray[0] = newTextString; if
     * (!"".equals(newTextString)) {
     * thisTextAttribute.addStringValue(newTextString); }
     */

    // add the text of test cases
//    for (int i = 0; i < testText.length; i++) {
//      thisTextAttribute.addStringValue(testText[i]);
//    }

    TestClassifier4 classifier = new TestClassifier4(classifierName, inputText, inputClasses);

    System.out.println("DATA SET:\n");
    System.out.println(classifier.classify());

    System.out.println("NEW CASES:\n");
    System.out.println(classifier.classifyNewCases(testText, testClasses));
  } // end main

  //
  // constructor
  //
  TestClassifier4(String classifierName, String[] inputText, String[] inputClasses) {
    this.classifierName = classifierName;
    this.inputText = inputText;
    this.inputClasses = inputClasses;
    
    classSet1 = new ClassSet();
    for (String classValue : inputClasses) {
      classSet1.add(classValue);
    }
    this.classAttribute = classSet1.buildClassAttribute();

    dictionary = new Dictionary2();
    for (int i = 0; i < inputText.length; i++) {
      String[] words = inputText[i].split("[\\s.,:'\\\"()?!]+");
      for (int j = 0; j < words.length; j++) {
        dictionary.resolve(words[j]);
      }
    }
    
    //
    // create the attribute information
    //
    attributeInfo = new ArrayList<>(dictionary.size() + 1);
    attributeInfo.add(classAttribute);
    for (String phrase : dictionary.phrases()) {
      System.out.println("... <" + phrase + ">");
      Attribute attribute = new Attribute(phrase);
      attributeInfo.add(attribute);
    }
//  List<String> inputTextVector = null; // null -> String type
//  Attribute thisTextAttribute = new Attribute("text", inputTextVector);
//  for (int i = 0; i < inputText.length; i++) {
//    thisTextAttribute.addStringValue(inputText[i]);
//  }
  }

  //
  // the real classify method
  //
  public StringBuffer classify() {

    StringBuffer result = new StringBuffer();

    // creates an empty instances set
    instances = new Instances("data set", attributeInfo, 100);

    // set which attribute is the class attribute
    instances.setClassIndex(0);
    
    try {

      instances = populateInstances(inputText, inputClasses, instances);
      result.append("DATA SET:\n" + instances + "\n");

      // make filtered SparseData
      //filteredData = filterText(instances);

      // create Set of modelWords
      //modelWords = new HashSet<String>();
      //Enumeration<Attribute> enumx = filteredData.enumerateAttributes();
      //while (enumx.hasMoreElements()) {
      //  Attribute att = (Attribute)enumx.nextElement();
      //  String attName = att.name().toLowerCase();
      //  modelWords.add(attName);
      //}

      //
      // Classify and evaluate data
      //
      classifier = AbstractClassifier.forName(classifierName, null);

      classifier.buildClassifier(instances);
      evaluation = new Evaluation(instances);
      evaluation.evaluateModel(classifier, instances);

      result.append(printClassifierAndEvaluation(classifier, evaluation) + "\n");

      // check instances
      int startIx = 0;
      result.append(checkCases(instances, classifier, classAttribute, inputText, "not test", startIx) + "\n");
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return result;

  } // end classify

  //
  // test new unclassified examples
  //
  public StringBuffer classifyNewCases(String[] testText, String[] testClasses) {

    StringBuffer result = new StringBuffer();

    // first copy the old instances,
    // then add the test words
    Instances newInstances = new Instances(instances);
    newInstances.setClassIndex(0);

    try {
      newInstances = populateInstances(testText, testClasses, newInstances);
      result.append("COMBINED DATA SET:\n" + newInstances + "\n");

      int startIx = instances.numInstances();
      result.append(checkCases(newInstances, classifier, classAttribute, testText, "new case", startIx) + "\n");

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    return result;

  } // end classifyNewCases

  //
  // from empty instances populate with text and class arrays
  //
  public Instances populateInstances(String[] inputTexts, String[] inputClasses, Instances dataset) {

    for (int i = 0; i < inputTexts.length; i++) {
      System.out.println("#### " + inputTexts[i]);
      //Instance inst = newInstance(inputTexts[i], inputClasses[i]);
      Instance inst2;
      if (inputClasses != null) {
        inst2 = newInstance2(inputTexts[i], inputClasses[i]);
      } else {
        inst2 = newInstance2(inputTexts[i], null);
      }
      System.out.println(inst2);
      inst2.setDataset(dataset);
      if (inputClasses != null) {
        inst2.setClassValue(classSet1.indexOf(inputClasses[i]));
      } else {
        inst2.setClassValue(2);
      }
      dataset.add(inst2);
    }
    return dataset;
  } // populateInstances buy from hey me this 
    // {3 1,5 1,6 1,7 1,8 1}

  
//  public Instance newInstance(String inputText, String inputClass) {
//
//    Instance inst = new SparseInstance(2);
//    if (inputClass != null) {
//      inst.setValue(classAttribute, inputClass);
//    }
//    inst.setValue(textAttribute, inputText);
//    return inst;
//  }

  
  public Instance newInstance2(String inputText, String inputClass) {
    SparseInstance inst = new SparseInstance(dictionary.size() + 1);
    
    String[] words = inputText.split("[\\s.,:'\\\"()?!]+");
    System.out.println("#### " + inputText + " " + words.length);

    for (int i = 0; i < words.length; i++) {
      int wordIndex = dictionary.indexOf(words[i]);
      System.out.println("... " + wordIndex + "  " + words[i]);
      if (wordIndex > 0) {
        inst.setValue(wordIndex, 1.0);
      }
    }
    
//    if (inputClass != null) {
//      inst.setValue(words.length, inputClass);
//    }buy from hey me this 
//    {3 1,5 1,6 1,7 1,8 1}
    System.out.println("############ " + inst);
    return inst;
  }

  //
  // check instances (full set or just test cases)
  //
  public static StringBuffer checkCases(Instances theseInstances, Classifier thisClassifier,
      Attribute thisClassAttribute, String[] texts, String testType, int startIx) {

    StringBuffer result = new StringBuffer();

    try {

      result.append("\nCHECKING ALL THE INSTANCES:\n");

      Enumeration<?> enumClasses = thisClassAttribute.enumerateValues();
      result.append("Class values (in order): ");
      while (enumClasses.hasMoreElements()) {
        String classStr = (String)enumClasses.nextElement();
        result.append("'" + classStr + "'  ");
      }
      result.append("\n");

      // startIx is a fix for handling text cases
      for (int i = startIx; i < theseInstances.numInstances(); i++) {

        SparseInstance sparseInst = new SparseInstance(theseInstances.instance(i));
        sparseInst.setDataset(theseInstances);

        result.append("\nTesting: '" + texts[i - startIx] + "'\n");
        // result.append("SparseInst: " + sparseInst + "\n");

        double correctValue = (double)sparseInst.classValue();
        double predictedValue = thisClassifier.classifyInstance(sparseInst);

        String predictString = thisClassAttribute.value((int)predictedValue) + " (" + predictedValue + ")";
        result.append("predicted: '" + predictString);
        // print comparison if not new case
        if (!"newcase".equals(testType)) {
          String correctString = thisClassAttribute.value((int)correctValue) + " (" + correctValue + ")";
          String testString = ((predictedValue == correctValue) ? "OK!" : "NOT OK!") + "!";
          result.append("' real class: '" + correctString + "' ==> " + testString);
        }
        result.append("\n");

        /*
         * if (thisClassifier instanceof Distribution) { double[] dist =
         * ((Distribution)thisClassifier).distributionForInstance(sparseInst);
         * 
         * // weight the levels into a spamValue double weightedValue = 0; //
         * experimental result.append("probability distribution:\n");
         * NumberFormat nf = NumberFormat.getInstance();
         * nf.setMaximumFractionDigits(3); for (int j = 0; j < dist.length; j++)
         * { result.append(nf.format(dist[j]) + " "); weightedValue +=
         * 10*(j+1)*dist[j]; if (j < dist.length -1) { result.append(",  "); } }
         * result.append("\nWeighted Value: " + nf.format(weightedValue) +
         * "\n"); }
         */

        result.append("\n");
        // result.append(thisClassifier.dumpDistribution());
        // result.append("\n");
      }

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    return result;

  } // end checkCases

//  //
//  // take instances in normal format (strings) and convert to Sparse format
//  //
//  public static Instances filterText(Instances theseInstances) {
//
//    StringToWordVector filter = null;
//    // default values according to Java Doc:
//    int wordsToKeep = 1000;
//
//    Instances filtered = null;
//
//    try {
//
//      filter = new StringToWordVector(wordsToKeep);
//      // we ignore this for now...
//      // filter.setDelimiters(delimitersStringToWordVector);
//      filter.setOutputWordCounts(false);
//      filter.setSelectedRange("1");
//
//      filter.setInputFormat(theseInstances);
//
//      filtered = weka.filters.Filter.useFilter(theseInstances, filter);
//      // System.out.println("filtered:\n" + filtered);
//
//    } catch (Exception ex) {
//      throw new RuntimeException(ex);
//    }
//
//    System.out.println("+++++++++++++++");
//    int n = filtered.numAttributes();
//    String[] attNames = new String[n];
//    for (int i = 0; i < n; i++) {
//      Attribute a = filtered.attribute(i);
//      attNames[i] = a.name();
//      System.out.println(a.name());
//    }
//    System.out.println("+++++++++++++++");
//    int m = filtered.numInstances();
//    for (int j = 0; j < m; j++) {
//      Instance x = filtered.instance(j);
//      for (int k = 0; k < x.numValues(); k++) {
//        System.out.print(attNames[x.index(k)] + " ");
//      }
//      System.out.println();
//      System.out.println(x.toString());
//    }
//    System.out.println("+++++++++++++++");
//    return filtered;
//
//  } // end filterText

  //
  // information about classifier and evaluation
  //
  public static StringBuffer printClassifierAndEvaluation(Classifier thisClassifier, Evaluation thisEvaluation) {

    StringBuffer result = new StringBuffer();

    try {
      result.append("\n\nINFORMATION ABOUT THE CLASSIFIER AND EVALUATION:\n");
      result.append("\nclassifier.toString():\n" + thisClassifier.toString() + "\n");
      result.append(
          "\nevaluation.toSummaryString(title, false):\n" + thisEvaluation.toSummaryString("Summary", false) + "\n");
      result.append("\nevaluation.toMatrixString():\n" + thisEvaluation.toMatrixString() + "\n");
      result.append("\nevaluation.toClassDetailsString():\n" + thisEvaluation.toClassDetailsString("Details") + "\n");
      result.append("\nevaluation.toCumulativeMarginDistribution:\n"
          + thisEvaluation.toCumulativeMarginDistributionString() + "\n");
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

    return result;

  } // end printClassifierAndEvaluation

} // end class TextClassifier
