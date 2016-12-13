package org.gyfor.docstore.parser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;
import org.gyfor.docstore.parser.IImageParser;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.gyfor.docstore.DocumentContents;
import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.PartialSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
public class TesseractImageOCR implements IImageParser {

  private Logger logger = LoggerFactory.getLogger(TesseractImageOCR.class);
  
  private IDocumentContents readOCRResults(int pageIndex, Path resultsFile) {
    XMLInputFactory2 factory = null ;
    try {
      factory = (XMLInputFactory2)XMLInputFactory2.newInstance();
      factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
      factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
      factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
      factory.setProperty(XMLInputFactory.IS_COALESCING, false);
 
      Reader resultsReader = Files.newBufferedReader(resultsFile);
      XMLStreamReader reader = factory.createXMLStreamReader(resultsReader);
      
      DocumentContents docContents = new DocumentContents();
      PartialSegment lineSegment = null;
      PartialSegment wordSegment = null;
      int level = 0;
      int lineLevel = 0;
      
      while (reader.hasNext()) {
        int event = reader.next();
        switch (event) {
        case XMLStreamConstants.START_DOCUMENT :
          break;
        case XMLStreamConstants.START_ELEMENT :
          level++;
          String klass = reader.getAttributeValue(null, "class");
          if (klass != null) {
            switch (klass) {
            case "ocr_line" :
              lineSegment = null;
              lineLevel = level;
              break;
            case "ocrx_word" :
              // Create an empty segment with the word's bounding box.  Attribute "title" contains the 
              // bound box dimensions.
              wordSegment = new PartialSegment(pageIndex, reader.getAttributeValue(null, "title"));
              break;
            }
          }
          break;
        case XMLStreamConstants.CHARACTERS :
          String word = reader.getText().trim();
          if (word.length() != 0) {
            if (lineSegment != null && lineSegment.overlaps(wordSegment, word)) {
              if (lineSegment.almostAdjacent(wordSegment, word)) {
                // If the two segments are almost adjacent, append the text without a space
                lineSegment.extendWide(wordSegment, word);
              } else {
                // ... otherwise add a space between the two segments
                lineSegment.extendWide(wordSegment, ' ', word);
              }
            } else {
              // Add the existing, completed, line segment to the document before starting a new one.
              if (lineSegment != null) {
                docContents.add(lineSegment);
                logger.info("Adding line segment:{}", lineSegment);
              }
              lineSegment = new PartialSegment(wordSegment, word);
            }
          }
          break;
        case XMLStreamConstants.END_ELEMENT :
          if (lineLevel == level && lineSegment != null) {
            logger.info("Adding line segmnt: {}", lineSegment);
            docContents.add(lineSegment);
          }
          level--;
          break;
        }
      }
      reader.close();
      resultsReader.close();
      
      return docContents;
    } catch (FactoryConfigurationError | XMLStreamException | IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  @Override
  public IDocumentContents parse(String id, int pageIndex, Path imagePath) {
    logger.info("Starting Tesseract OCR of: {}, page {}", imagePath, pageIndex);

    Path ocrBase = OCRPaths.getBasePath(id);

    String tesseractHome = System.getenv("TESSDATA_PREFIX");
    if (tesseractHome == null) {
      throw new RuntimeException("Environment variable 'TESSDATA_PREFIX' not set");
    }
    String[] cmd = { tesseractHome + "/tesseract", imagePath.toString(), ocrBase.toString(), "hocr" };
    //logger.info("Starting Tesseract OCR: " + cmd[0] + "|" + cmd[1] + "|" + cmd[2] + "|" + cmd[3]);
    Runtime runtime = Runtime.getRuntime();
    try {
      Process process = runtime.exec(cmd);
      int result = process.waitFor();
      if (result != 0) {
        InputStream errorStream = process.getErrorStream();
        byte[] buffer = new byte[1024];
        int n = errorStream.read(buffer);
        while (n > 0) {
          System.err.print(new String(buffer, 0, n));
          n = errorStream.read(buffer);
        }
        throw new RuntimeException("Return value " + result);
      }
    } catch (IOException | InterruptedException | RuntimeException ex) {
      throw new RuntimeException(cmd[0] + " " + cmd[1] + " " + cmd[2] + ": " + ex);
    }

    // Now do something with the lines extracted by Tesseract
    Path hocrFile = OCRPaths.getHocrPath(id);
    logger.info("Starting parse of html file from OCR: " + hocrFile);
    IDocumentContents docInstance = readOCRResults(pageIndex, hocrFile);
    
    // The readOCRResults method is done with the hocrFile, so we can get rid of it.
///    try {
      System.out.println("#### " + hocrFile.toAbsolutePath().toString());
      /////////////// TODO Files.delete(hocrFile);
///    } catch (IOException ex) {
///      throw new RuntimeException(ex);
///    }
    
    return docInstance;
    
//    // Now do something with the lines extracted by Tesseract
//    String textFileName = FileName.replaceExtn(imageFile, ".txt");
//    File textFile = new File(textFileName);
//
//    try (BufferedReader reader = new BufferedReader(new FileReader(textFileName))) {
//      String line = reader.readLine();
//      while (line != null) {
//        wordSink.addWord(line);
//        line = reader.readLine();
//      }
//      reader.close();
//      if (deleteWhenDone) {
//        textFile.delete();
//      }
//    } catch (IOException ex) {
//      throw new RuntimeException(ex);
//    }
  }
}
