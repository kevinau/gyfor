package org.gyfor.docstore.parser.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.gyfor.docstore.Dictionary;
import org.gyfor.docstore.IDocumentContents;
import org.gyfor.docstore.IDocumentStore;
import org.gyfor.docstore.parser.IImageParser;
import org.gyfor.docstore.parser.IPDFParser;
import org.gyfor.util.Digest;
import org.gyfor.util.MD5DigestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentContentsBuilder {

  private static final Logger logger = LoggerFactory.getLogger(DocumentContentsBuilder.class);
  
  private final IPDFParser pdfParser;
  private final IImageParser imageParser;
  
  
  public DocumentContentsBuilder (IPDFParser pdfParser, IImageParser imageParser) {
    this.pdfParser = pdfParser;
    this.imageParser = imageParser;
  }

  
  public IDocumentContents buildContent (Digest digest, boolean forceRegen, IDocumentStore docStore) throws IOException {
    Path contentsPath = docStore.getContentsPath(digest);
    if (Files.exists(contentsPath)) {
      // The contents file exists.  As the file is immutable, if it eixists it does not need to
      // to be regenerated.
    } else {
      forceRegen = true;
    }
    
    Path path = docStore.getSourcePath(digest);
    IDocumentContents docContents;
    if (forceRegen) {
      logger.info("Importing {}", path.getFileName());
      String fileName = path.getFileName().toString();
      int m = fileName.lastIndexOf('.');
      String extn;
      if (m == -1) {
        extn = "";
      } else {
        extn = fileName.substring(m).toLowerCase();
      }

      switch (extn) {
      case ".pdf" :
        docContents = pdfParser.parse(digest, path, 150, docStore);
        break;
      case ".png" :
      case ".jpeg" :
      case ".jpg" :
      case ".jfif" :
      case ".tiff" :
      case ".pnm" :
      case ".bmp" :
        docContents = imageParser.parse(digest, path);
        break;
      default :
        throw new RuntimeException("File type: " + fileName + " not supported");
      }
      logger.info("Saving document contents");
      docContents.save(contentsPath);
    } else {
      logger.info("Use previously saved document contents: {}", path.getFileName());
      docContents = IDocumentContents.load(contentsPath);
    }
    return docContents;
  }

  
  public void buildContents (List<Path> files, Dictionary dictionary, IDocumentStore docStore) {
    for (Path fileEntry : files) {
      IDocumentContents docContents;
      try {
        String id = new MD5DigestFactory().getFileHash(fileEntry).toString();
        docContents = buildContent(id, false, docStore);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
      if (dictionary != null) {
        docContents.updateDictionary(dictionary);
      }
    }
  }

  
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
