/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.plcore.docstore.parser.impl;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.plcore.docstore.IDocumentStore;
import org.plcore.docstore.parser.IImageParser;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocumentContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extracts the images from a PDF file.
 *
 * @author Ben Litchfield
 */
final class PDFImageExtractor {
  
  private static final Logger logger = LoggerFactory.getLogger(PDFImageExtractor.class);
  
  private final IImageParser imageParser;
  private final int dpi;
  private final Set<PDImageXObject> seen = new HashSet<>();
  
  PDFImageExtractor(IImageParser imageParser, int dpi) {
    this.imageParser = imageParser;
    this.dpi = dpi;
  }

  /**
   * Entry point for the application.
   *
   * @param args
   *          The command-line arguments.
   * @throws IOException
   *           if there is an error reading the file or extracting the images.
   */
//  public static void main(String[] args) throws IOException {
//    // suppress the Dock icon on OS X
//    System.setProperty("apple.awt.UIElement", "true");
//
//    IImageParser imageParser = new TesseractImageOCR();
//    PDFImageExtractor extractor = new PDFImageExtractor(imageParser, 150);
//    extractor.run(args);
//  }

//  private void run(String[] args) throws IOException {
//    //String pdfFile = args[0];
//    String pdfFile = "c:/PennyLedger/1d51-9fe1b211e8039458b2ac4dbbfbf1.pdf";
//    if (pdfFile.length() <= 4) {
//      throw new IllegalArgumentException("Invalid file name: not PDF");
//    }
//    String password = "";
//    Path pdfPath = Paths.get(pdfFile);
//    PDDocument document = PDDocument.load(pdfPath.toFile(), password);
//    IDocumentContents docContents = new DocumentContents();
//    extract(document, id, pdfPath, docContents);
//  }


  ISourceDocumentContents extract(PDDocument document, String id, ISourceDocumentContents docContents) throws IOException {
    AccessPermission ap = document.getCurrentAccessPermission();
    if (!ap.canExtractContent()) {
      throw new IOException("You do not have permission to extract images");
    }

    for (int i = 0; i < document.getNumberOfPages(); i++) {
      PDPage page = document.getPage(i);
      logger.info("Extract image: " + page + " " + i + " " + id);
      ImageGraphicsEngine extractor = new ImageGraphicsEngine(page, i, id);
      extractor.run();
      ISourceDocumentContents pageContents = extractor.getPageContents();
      docContents = docContents.merge(pageContents);
    }
    return docContents;
  }
  
  
  private class ImageGraphicsEngine extends PDFGraphicsStreamEngine {
    private final int pageIndex;
    private final String id;
    private ISourceDocumentContents pageContents;
    private int imageIndex = 0;
    
    
    protected ImageGraphicsEngine(PDPage page, int pageIndex, String id) throws IOException {
      super(page);
      this.pageContents = new SourceDocumentContents();
      this.pageIndex = pageIndex;
      this.id = id;
    }

    public void run() throws IOException {
      processPage(getPage());
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException {
      if (pdImage instanceof PDImageXObject) {
        PDImageXObject xobject = (PDImageXObject) pdImage;
        if (seen.contains(xobject)) {
          // skip duplicate image
          return;
        }
        seen.add(xobject);
      }

      /* ---------------------------------------------------------------- */
      //PDImageXObject image2 = (PDImageXObject)pdImage;
      int imageWidth2 = pdImage.getWidth();
      int imageHeight2 = pdImage.getHeight();
      System.out.println("*******************************************************************");

      Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
      float imageXScale = ctmNew.getScalingFactorX();
      float imageYScale = ctmNew.getScalingFactorY();

      // position in user space units. 1 unit = 1/72 inch at 72 dpi
      System.out.println("position in PDF = " + ctmNew.getTranslateX() + ", " + ctmNew.getTranslateY() + " in user space units");
      // raw size in pixels
      System.out.println("raw image size  = " + imageWidth2 + ", " + imageHeight2 + " in pixels");
      // displayed size in user space units
      System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in user space units");
//      // displayed size in inches at 72 dpi rendering
//      imageXScale /= 72;
//      imageYScale /= 72;
//      System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in inches at 72 dpi rendering");
//      // displayed size in millimeters at 72 dpi rendering
//      imageXScale *= 25.4;
//      imageYScale *= 25.4;
//      System.out.println("displayed size  = " + imageXScale + ", " + imageYScale + " in millimeters at 72 dpi rendering");
      System.out.println();
      /* ---------------------------------------------------------------- */

      //Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
      //float x = ctm.getTranslateX();
      //float y = ctm.getTranslateY();

      //x = 59;
      //y = 62;
      //x *= 72 / 25.4;
      //y *= 72 / 25.4;
      //logger.info("Extracting image no. {}; at {},{}; from page {} of: {}", imageIndex, x, y, pageIndex, id);

      int imageWidth = pdImage.getWidth();
      int imageHeight = pdImage.getHeight();
      if (imageWidth > 1 && imageHeight > 1) {
        // Only OCR significant images
        PDGraphicsState gs = getGraphicsState();
        Matrix gm = gs.getCurrentTransformationMatrix();
      
        BufferedImage image = pdImage.getImage();
        logger.info("Image size: " + pdImage.getWidth() + "  " + pdImage.getHeight());
        Path ocrImagePath = OCRPaths.getOCRImagePath(id, pageIndex, imageIndex);
        ImageIO.writeImage(image, ocrImagePath);
        ISourceDocumentContents imageContents = imageParser.parse(id, pageIndex, ocrImagePath);
        float pageWidth = gm.getScaleX() + gm.getTranslateX();
        
        // Scale the image down to page width (at 72dpi), and then scale to the dpi we want.
        double scale = (pageWidth / imageWidth) * (dpi / 72.0);
        imageContents.scaleSegments(scale * IDocumentStore.IMAGE_SCALE);
        pageContents = pageContents.merge(imageContents);
        //////////Files.delete(ocrImagePath);
      }
      imageIndex++;
    }

    
    public ISourceDocumentContents getPageContents () {
      return pageContents;
    }
    
    
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) throws IOException {
      logger.info("Append rectangle");
    }

    @Override
    public void clip(int windingRule) throws IOException {
      logger.info("Clip");
    }

    @Override
    public void moveTo(float x, float y) throws IOException {
      logger.info("Moving to {}, {} from page {} of: {}", x, y, pageIndex, id);
    }

    @Override
    public void lineTo(float x, float y) throws IOException {
      logger.info("Line to {}, {}", x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
      logger.info("Curve to");
    }

    @Override
    public Point2D getCurrentPoint() throws IOException {
      //logger.info("Get current point");
      return new Point2D.Float(0, 0);
    }

    @Override
    public void closePath() throws IOException {
      // logger.info("close path");
    }

    @Override
    public void endPath() throws IOException {
      // logger.info("end path");
    }

    @Override
    public void strokePath() throws IOException {
      // logger.info("stroke path");
    }

    @Override
    public void fillPath(int windingRule) throws IOException {
      // logger.info("fill path");
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
      // logger.info("fill and stroke path");
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
      // logger.info("shading fill");
    }
  }

}
