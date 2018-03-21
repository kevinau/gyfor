package org.plcore.docstore.parser.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.plcore.docstore.IDocumentStore;


public class PDFToImage {

  public PDFToImage() {
  }

  
  public static void buildImageFile (String id, Path pdfPath, int dpi, String password, IDocumentStore docStore) {
    
    try (PDDocument document = PDDocument.load(pdfPath.toFile(), password)) {
      ImageType imageType = ImageType.RGB;
      
      int endPage = document.getNumberOfPages();
      PDFRenderer renderer = new PDFRendererNoText(document);
      BufferedImage singleImage = null;
      for (int i = 0; i < endPage; i++) {
        System.out.println("............" + renderer.getClass());
        BufferedImage image = renderer.renderImageWithDPI(i, dpi, imageType);
        singleImage = ImageIO.appendImage(singleImage, image);
        Path imagePath = docStore.newViewImagePath(id, i);
        ImageIO.writeImage(singleImage, imagePath);
      }
      
      // Resize the image to the required dpi
//      if (dpi != 150) {
//        double scale = dpi / 150.0;
//        int width = (int)(singleImage.getWidth() * scale + 0.5);
//        int height = (int)(singleImage.getHeight() * scale + 0.5);
//        singleImage = Scalr.resize(singleImage, Scalr.Method.ULTRA_QUALITY,
//            Scalr.Mode.AUTOMATIC, width, height);
//      }
      
      document.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  public static void buildImageFile (String id, Path pdfFile, int dpi, IDocumentStore docStore) {
    buildImageFile (id, pdfFile, dpi, "", docStore);
  }
  

//  public static void buildImageFile (String id, Path pdfFile, IDocumentStore docStore) {
//    buildImageFile (id, pdfFile, 150, "", docStore);
//  }
  

//  public static void main(String[] args) throws Exception {
//    PDFToImage review = new PDFToImage();
//    Path pdfPath = Paths.get("C:/Users/Kevin/Accounts/JH Shares/Telstra/2010-09-24.pdf");
//    Digest digest = new CRC64DigestFactory().getFileDigest(pdfPath);
//    //pdfFile = Paths.get("C:/Users/Kevin/Accounts/AU/Commonwealth/Business Transaction Account/Statement20100730.pdf");
//    DocumentStore docStore = new DocumentStore();
//    docStore.activate(null);
//    review.buildImageFile(digest, pdfPath, 150, docStore);
//  }
}
