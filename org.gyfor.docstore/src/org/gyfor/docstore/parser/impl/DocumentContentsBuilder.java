package org.gyfor.docstore.parser.impl;

import java.nio.file.Path;

import org.gyfor.doc.IDocumentContents;
import org.gyfor.doc.IDocumentStore;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentContentsBuilder {

  private static final Logger logger = LoggerFactory.getLogger(DocumentContentsBuilder.class);
 
  public IDocumentContents buildContent (String hashCode, String extn, IDocumentStore docStore) {
    Path path = docStore.getSourcePath(hashCode, extn);
    IDocumentContents docContents;

    logger.info("Parsing {} to extact textual contents", path.getFileName());

    if (docStore.isImageFile(extn)) {
      IImageParser imageParser = new TesseractImageOCR();
      docContents = imageParser.parse(hashCode, 0, path);
    } else {
      switch (extn) {
      case ".pdf" :
        IImageParser imageParser = new TesseractImageOCR();
        IPDFParser pdfParser = new PDFBoxPDFParser(imageParser);
        docContents = pdfParser.parse(hashCode, path, IDocumentStore.IMAGE_RESOLUTION, docStore);
        break;
      default :
        throw new RuntimeException("File type: " + path + " not supported");
      }
    }
  
    return docContents;
  }

  
//  public void buildContents (List<Path> files, Dictionary dictionary, IDocumentStore docStore) {
//    for (Path fileEntry : files) {
//      IDocumentContents docContents;
//      try {
//        String id = new MD5DigestFactory().getFileHash(fileEntry).toString();
//        docContents = buildContent(id, extn, docStore);
//      } catch (IOException ex) {
//        throw new RuntimeException(ex);
//      }
//      if (dictionary != null) {
//        docContents.updateDictionary(dictionary);
//      }
//    }
//  }

  
//  public void directorySearchBuildContents (Path dir, String pattern, Dictionary dictionary, List<Path> hashNamedFiles) {
//    logger.info("Build document contents.  Searching for {} within {}", pattern, dir);
//
//    List<Path> sourceFiles = new ArrayList<>();
//    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, pattern)) {
//      for (Path fileEntry : stream) {
//        sourceFiles.add(fileEntry);
//      }
//    } catch (IOException ex) {
//      throw new RuntimeException(ex);
//    }
//    logger.info("Found {} files", sourceFiles.size());
//    
//    int copied = FileIO.conditionallyCopyFiles(sourceFiles,  hashNamedFiles);
//    logger.info("Copied {} files", copied);
//
//    buildContents(hashNamedFiles, dictionary);
//  }

}
