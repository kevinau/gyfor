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
import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.IDocumentStore;
import org.gyfor.doc.PageImage;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.gyfor.util.CRC64DigestFactory;
import org.gyfor.util.DigestFactory;
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


  private IDocumentContents extractImages(PDDocument document, int dpi, String id, Path pdfPath, IDocumentContents docContents)
      throws IOException {
    logger.info("Extract images from PDF document: " + pdfPath.getFileName());

    PDFImageExtractor imageExtractor = new PDFImageExtractor(imageParser, dpi);
    docContents = imageExtractor.extract(document, id, docContents);
    return docContents;
  }

  
  private IDocumentContents extractImages2(PDDocument pdDocument, int dpi, String id, Path pdfPath, IDocumentContents docContents)
      throws IOException {
    logger.info("Extract images from PDF document: " + pdfPath.getFileName());

    PDFRendererNoText renderer = new PDFRendererNoText(pdDocument);
    int endPage = pdDocument.getNumberOfPages();
    for (int i = 0; i < endPage; i++) {
      System.out.println("======================= dpi " + dpi);
      dpi = 300;
      BufferedImage image = renderer.renderImageWithDPI(i, dpi, ImageType.BINARY);
      System.out.println("Image size in pixels: " + image.getWidth() + " x " + image.getHeight());
      System.out.println("A4 size in pixels (@300 dpi): " + "2480 x 3508");
      Path imageFile = OCRPaths.getOCRImagePath(id, i);
      //ImageIO.writeImage(image, imageFile);
      ImageIOUtil.writeImage(image, imageFile.toString(), dpi);
      IDocumentContents imageDocContents = imageParser.parse(id, i, imageFile);
      double scale = dpi / 72;
      scale = 1.2;
      System.out.println("======================= " + scale + " " + IDocumentStore.IMAGE_SCALE);
      imageDocContents.scaleSegments(scale * IDocumentStore.IMAGE_SCALE);
      docContents = docContents.merge(imageDocContents);
    }
    return docContents;
  }

  
  @Override
  public IDocumentContents parseText(String id, Path pdfPath, int dpi, IDocumentStore docStore) {
    //DigestFactory digestFactory = new CRC64DigestFactory();

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
  public IDocumentContents parse(Path pdfPath, int dpi) {
    DigestFactory digestFactory = new CRC64DigestFactory();
    String id = digestFactory.getFileDigest(pdfPath).toString();
    
    PDDocument pdDocument = null;
    try {
      InputStream input = Files.newInputStream(pdfPath);
      pdDocument = PDDocument.load(input);

      // create PDFTextStipper to convert PDF to Text
      PDFTextStripper3 pdfTextStripper = new PDFTextStripper3();
      pdfTextStripper.getText(pdDocument);

      IDocumentContents textContents = pdfTextStripper.getDocumentContents();
      // The default for renderImage is 72dpi, so adjust the segment locations
      // to match our scale.
      double scale = dpi / 72;
      textContents.scaleSegments(scale * IDocumentStore.IMAGE_SCALE);

      // Add segments OCR'd from images within the PDF document
      IDocumentContents docContents = extractImages(pdDocument, dpi, id, pdfPath, textContents);
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
      IDocumentContents docContents = extractImages2(pdDocument, dpi, id, pdfPath, textContents);

      // Render PDF as an image for viewing
      PDFRenderer renderer = new PDFRenderer(pdDocument);
      int endPage = pdDocument.getNumberOfPages();
      for (int i = 0; i < endPage; i++) {
        BufferedImage image = renderer.renderImageWithDPI(i, dpi, ImageType.RGB);
        if (i == 0) {
          // Create a thumbnail of first page
          Path thumbsFile = docStore.getThumbsImagePath(id);
          ImageIO.writeThumbnail(image, thumbsFile);
        }
        Path imageFile = docStore.newViewImagePath(id, i);
        ImageIO.writeImage(image, imageFile);
///////////////////////////////////////////////        ImageIOUtil.writeImage(image, imageFile.toString(), dpi);
        PageImage pageImage = new PageImage(i, image.getWidth(), image.getHeight());
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
