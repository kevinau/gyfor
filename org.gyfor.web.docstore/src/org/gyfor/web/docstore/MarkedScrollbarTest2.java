package org.gyfor.web.docstore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class MarkedScrollbarTest2 {

  private static class PDFMarkable implements IMarkable {

    @Override
    public int getOffset() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public String getLabel(int level) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public String getTargetId() {
      return file.toString();
    }
    
  }
  
  
  private LocalDate getPDFCreationDate (File file) {
    long creationTime;
    try (PDDocument doc = PDDocument.load(file))
    {
      PDDocumentInformation info = doc.getDocumentInformation();
      Calendar calendar = info.getCreationDate();
      if (calendar == null) {
        Path path = file.toPath();
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
        creationTime = attr.creationTime().toMillis();
      } else {
        creationTime = calendar.getTimeInMillis();
      }
      doc.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return LocalDate.ofEpochDay(creationTime / (24 * 60 * 60 * 1000));
  }
  
  
  private void findPDFs(File dir, List<File> found) {
    File[] files = dir.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        findPDFs(file, found);
      } else {
        String name = file.getName();
        if (name.toLowerCase().endsWith(".pdf")) {
          found.add(file);
        }
      }
    }
  }
  
  
  public static void main(String[] args) {
    MarkedScrollbarTest2 test = new MarkedScrollbarTest2();
    
    File top = new File("/Users/Kevin/Accounts/AU");
    List<File> found = new ArrayList<>();
    test.findPDFs(top, found);
    
    for (File file : found) {
      LocalDate creationDate = test.getPDFCreationDate(file);
      System.out.println(file + "  " + creationDate);
    }
  }
}
