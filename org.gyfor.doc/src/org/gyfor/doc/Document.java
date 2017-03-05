package org.gyfor.doc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.sql.Timestamp;


public class Document extends DocumentSummary implements Serializable {

  private static final long serialVersionUID = 2L;

  private Timestamp originTime;
  
  private String originName;

  private String originExtension;
  
  private IDocumentContents contents;
  

  public Document(String hashCode, Timestamp originTime, String originName, String originExtension, Timestamp importTime, IDocumentContents contents) {
    super (hashCode, importTime);
    this.originTime = originTime;
    this.originName = originName;
    this.originExtension = originExtension;
    this.contents = contents;
  }

  
  public Timestamp getOriginTime () {
    return originTime;
  }
  
  
  public String getOriginName () {
    return originName;
  }
  
  
  public String getOriginExtension() {
    return originExtension;
  }
  
  
  public void setContents (IDocumentContents contents) {
    this.contents = contents;
  }
  
  
  public IDocumentContents getContents () {
    return contents;
  }
  
  
  @Override
  public String toString() {
    return "Document[" + super.toString() + ", " + originName + ", " + originExtension + ", " + originTime + "]";
  }

  
  public void save(Path file) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(this);
    } catch (IOException ex) {
      throw new RuntimeException();
    }
  }

  
  public static Document load(Path file) {
    Document document;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      document = (Document)ois.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
    return document;
  }


}
