package org.gyfor.docstore.parser.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PDFBoxPDFParser implements IPDFParser {

  private static Logger logger = LoggerFactory.getLogger(PDFBoxPDFParser.class);

  private IImageParser imageParser;

  
  public PDFBoxPDFParser() {
  }

  
  public PDFBoxPDFParser(IImageParser imageParser) {
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
      double scale = dpi / 72.0;
      textContents.scaleSegments(scale);

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
      }

      docContents.setPageCount(endPage);
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
