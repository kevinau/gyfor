package org.gyfor.docstore;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public interface IDocumentContents {

  public List<? extends ISegment> getSegments();

  
  public int size();


  public void updateDictionary(Dictionary dictionary);


  public default void scaleSegments(double d) {
    if (d != 1.0) {
      throw new AbstractMethodError("Not implemented");
    }
  }


  public IDocumentContents merge(IDocumentContents other);


  public void sortSegments();


  public default void save(Path file) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(this);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }


  public static IDocumentContents load(Path file) {
    IDocumentContents docInstance;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      docInstance = (IDocumentContents)ois.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
    return docInstance;
  }


  public void dump();


  public int getPageCount();


  public void setPageCount(int pageCount);

}
