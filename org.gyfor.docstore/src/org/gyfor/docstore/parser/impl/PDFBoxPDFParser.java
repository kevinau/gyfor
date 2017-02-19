package org.gyfor.docstore.parser.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.PageImage;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PDFBoxPDFParser implements IPDFParser {

  private static Logger logger = LoggerFactory.getLogger(PDFBoxPDFParser.class);

  private IImageParser imageParser;

  
  public PDFBoxPDFParser() {
    // Turn off logging
//    String[] loggers = { 
//        "org.apache.pdfbox.util.PDFStreamEngine",
//        "org.apache.pdfbox.pdmodel.font.PDSimpleFont",
//        "org.apache.pdfbox.pdmodel.font.PDFont",
//        "org.apache.pdfbox.pdmodel.font.FontManager",
//        "org.apache.pdfbox.pdfparser.PDFObjectStreamParser" };
//    for (String logger : loggers) {
//      org.apache.log4j.Logger logpdfengine = org.apache.log4j.Logger.getLogger(logger);
//      logpdfengine.setLevel(org.apache.log4j.Level.OFF);
//    }
  }

  
  public PDFBoxPDFParser(IImageParser imageParser) {
    this();
    this.imageParser = imageParser;
  }


  private IDocumentContents extractImages(PDDocument document, int dpi, String id, Path pdfPath, boolean keepOCRImageFile, IDocumentContents docContents)
      throws IOException {
    logger.info("Extract images from PDF document: " + pdfPath.getFileName());

    PDFImageExtractor imageExtractor = new PDFImageExtractor(imageParser, dpi);
    docContents = imageExtractor.extract(document, id, docContents);
    return docContents;
  }

  
  @Override
  public IDocumentContents parseText(String id, Path pdfPath, int dpi, IDocumentStore docStore) {
    PDDocument pdDocument = null;

    // create PDFTextStipper to convert PDF to Text
    PDFTextStripper3 pdfTextStripper;
    try {
      InputStream input = Files.newInputStream(pdfPath);
      pdDocument = PDDocument.load(input);

      pdfTextStripper = new PDFTextStripper3();
      pdfTextStripper.getText(pdDocument);

      IDocumentContents textContents = pdfTextStripper.getDocumentContents();
      // The default for renderImage is 72dpi, so adjust the segment locations
      // to match our scale.
      double scale = dpi / 72;
      textContents.scaleSegments(scale * IDocumentStore.IMAGE_SCALE);

      // Don't OCR images.  Don't create image files.
      return textContents;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } finally {
      if (pdDocument != null) {
        try {
          pdDocument.close();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }
  

  @Override
  public IDocumentContents parse(String id, Path pdfPath, int dpi, IDocumentStore docStore) {
    PDDocument pdDocument = null;
    try {
      InputStream input = Files.newInputStream(pdfPath);
      pdDocument = PDDocument.load(input);

      // create PDFTextStipper to convert PDF to Text
      PDFTextStripper3 pdfTextStripper = new PDFTextStripper3();
      pdfTextStripper.getText(pdDocument);

      ////int wordCount = pdfTextStripper.getWordCount();
      //////////////
      ////wordCount++;
      
      IDocumentContents textContents = pdfTextStripper.getDocumentContents();
      // The default for renderImage is 72dpi, so adjust the segment locations
      // to match our scale.
      double scale = dpi / 72;
      textContents.scaleSegments(scale * IDocumentStore.IMAGE_SCALE);

      // Add segments OCR'd from images within the PDF document
      IDocumentContents docContents = extractImages(pdDocument, dpi, id, pdfPath, false, textContents);

      // Render PDF as an image for viewing
      int endPage = pdDocument.getNumberOfPages();
      PDFRenderer renderer = new PDFRenderer(pdDocument);
      for (int i = 0; i < endPage; i++) {
        BufferedImage image = renderer.renderImageWithDPI(i, dpi, ImageType.RGB);
        if (i == 0) {
          // Create a thumbnail of first page
          Path thumbsFile = docStore.getThumbsImagePath(id);
          ImageIO.writeThumbnail(image, thumbsFile);
        }
        Path imageFile = docStore.newViewImagePath(id, i);
        ImageIO.writeImage(image, imageFile);
        ImageIOUtil.writeImage(image, imageFile.toString(), dpi);
        PageImage pageImage = new PageImage(image.getWidth(), image.getHeight());
        docContents.addPageImage(pageImage);
      }
      return docContents;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    } finally {
      if (pdDocument != null) {
        try {
          pdDocument.close();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
  }
  
 
}
