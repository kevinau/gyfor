package org.gyfor.docstore;

import java.io.Serializable;
import java.util.Date;

import org.gyfor.docstore.impl.DocumentContents;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Document implements Serializable {

  private static final long serialVersionUID = 2L;

  @PrimaryKey
  private long crc64;
  
  private Date originTime;
  
  private String originName;

  private String originExtension;
  
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private final Date importTime;

  private IDocumentContents contents;
  
  
  public Document() {
    this.importTime = new Date();
  }


  public Document(long crc64, Date originTime, String originName, String originExtension) {
    this.crc64 = crc64;
    this.originTime = originTime;
    this.originName = originName;
    this.originExtension = originExtension;
    this.importTime = new Date();
    this.contents = new DocumentContents();
  }

  
  public long getDigest () {
    return crc64;
  }
  
  
  public String getOriginExtension() {
    return originExtension;
  }
  

  @Override
  public String toString() {
    return "Document[" + getDigest() + ", " + originName + ", " + originExtension + ", " + originTime + "]";
  }
  
}
