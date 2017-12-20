package org.gyfor.doc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.List;

import org.gyfor.nio.SafeOutputStream;

public class DocumentData {

  private IDocumentType docType;
  
  private List<DocumentDataItem> items;

  public void setDocumentType(IDocumentType docType) {
    this.docType = docType;
  }
  
  
  public void save(Path file) {
    try (SafeOutputStream os = new SafeOutputStream(file);
         ObjectOutputStream oos = new ObjectOutputStream(os)) {
      oos.writeObject(this);
      os.commit();
    } catch (IOException ex) {
      throw new RuntimeException();
    }
  }

  
  public static DocumentData load(Path file) {
    DocumentData d;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      d = (DocumentData)ois.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
    return d;
  }

}
