package org.gyfor.docstore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Document implements Serializable {

  private static final long serialVersionUID = 2L;

  @PrimaryKey
  private String id;
  
  private String type;
  
  private Date originTime;
  
  private String originName;

  private String originExtension;
  
  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  private final Date importTime;

  private IDocumentContents contents;
  
  private List<PageImage> pageImages;
  
  @SecondaryKey(relate = Relationship.MANY_TO_ONE, relatedEntity = Party.class, onRelatedEntityDelete = DeleteAction.NULLIFY)
  private int partyId;
  
  
  public Document() {
    this.importTime = new Date();
  }


  public Document(String id, Date originTime, String originName, String originExtension, IDocumentContents contents) {
    this.id = id;
    this.type = "Unclassified";
    this.originTime = originTime;
    this.originName = originName;
    this.originExtension = originExtension;
    this.importTime = new Date();
    this.contents = contents;
    this.pageImages = new ArrayList<>();
  }

  
  public String getId () {
    return id;
  }
  
  
  public String getType () {
    return type;
  }
  
  
  public Date getOriginTime () {
    return originTime;
  }
  
  
  public String getOriginName () {
    return originName;
  }
  
  
  public String getOriginExtension() {
    return originExtension;
  }
  
  
  public IDocumentContents getContents () {
    return contents;
  }
  
  
  public int getPageCount () {
    return pageImages.size();
  }
  
  
  @Override
  public String toString() {
    return "Document[" + id + ", " + originName + ", " + originExtension + ", " + originTime + "]";
  }
  
}
