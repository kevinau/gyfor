package org.gyfor.docstore;

import java.io.Serializable;
import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Document implements Serializable {

  private static final long serialVersionUID = 2L;

  @PrimaryKey
  private String id;
  
  private Date originTime;
  
  private String originName;

  private String originExtension;
  
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private final Date importTime;

  private IDocumentContents contents;
  
  
  public Document() {
    this.importTime = new Date();
  }


  public Document(String id, Date originTime, String originName, String originExtension, IDocumentContents contents) {
    this.id = id;
    this.originTime = originTime;
    this.originName = originName;
    this.originExtension = originExtension;
    this.importTime = new Date();
    this.contents = contents;
  }

  
  public String getId () {
    return id;
  }
  
  
  public String getOriginExtension() {
    return originExtension;
  }
  
  
  public IDocumentContents getContents () {
    return contents;
  }
  

  @Override
  public String toString() {
    return "Document[" + id + ", " + originName + ", " + originExtension + ", " + originTime + "]";
  }
  
}
