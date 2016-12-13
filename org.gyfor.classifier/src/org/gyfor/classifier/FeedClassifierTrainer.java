package org.gyfor.classifier;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class FeedClassifierTrainer {

  public static void main(String[] args) {

    String modelFilePath= "C:/Users/Kevin/data/weka-model.bin";
   String trainingFile= "C:/Users/Kevin/data/weka-train.txt";
// Instance of openNLP's default model class
DoccatModel model = null;
InputStream dataIn = null;
try {
   dataIn = new FileInputStream(trainingFile);
   ObjectStream<String> lineStream = new PlainTextByLineStream(dataIn,
           "UTF-8");
   ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream( lineStream);
   // "en" is language code of English.
   model = DocumentCategorizerME.train("en", sampleStream);
} catch (IOException e) {
   log.error("Failed to read or parse training data, training failed",e);
} finally {
   if (dataIn != null) {
       try {
           // free the memory resources.
           dataIn.close();
       } catch (IOException e) {
           log.warn(e.getLocalizedMessage());
       }
   }
}
OutputStream modelOut = null;
try {
   modelOut = new BufferedOutputStream(new FileOutputStream(modelFilePath));
   model.serialize(modelOut);
} catch (IOException e) {
   log.error("Failed to save model at location "+modelFilePath);
} finally {
   if (modelOut != null) {
       try {
           modelOut.close();
       } catch (IOException e) {
           log.error("Failed to correctly save model. Written model might be invalid.");
       }
   }
}
    
    String content = "Document that needs to categorized goes here";

    String modelFilePath= . . .
        InputStream is = new FileInputStream(modelFilePath);
        DoccatModel model = new DoccatModel(is);
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);
        double[] outcomes = myCategorizer.categorize("string  to classify");
        String category = myCategorizer.getBestCategory(outcomes);

  }


  public void DocumentCategorizer(String text) throws IOException {

    File test = new File("Path to your en-doccat.bin model file");
    String classificationModelFilePath = test.getAbsolutePath();
    DocumentCategorizerME classificationME = new DocumentCategorizerME(
        new DoccatModel(new FileInputStream(classificationModelFilePath)));
    String documentContent = text;
    double[] classDistribution = classificationME.categorize(documentContent);

    String predictedCategory = classificationME.getBestCategory(classDistribution);
    System.out.println("Model prediction : " + predictedCategory);
  }
}