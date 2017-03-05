package org.gyfor.docstore;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.gyfor.math.Decimal;
import org.gyfor.sql.IConnection;
import org.gyfor.sql.IConnectionFactory;
import org.gyfor.sql.IPreparedStatement;
import org.gyfor.sql.IResultSet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


//@Component (service = DocumentDataAccessObject.class)
public class DocumentDataAccessObject {

  private static final String insert1Sql = "insert into document(id, hash_code, origin_time, origin_name, origin_extension, import_time)"
      + "                                   values (?,?,?,?,?,?) ";
  
  private static final String insert2Sql = "insert into document_segment(document_id, page_index, segment_id, x0, y0, x1, y1, font_size, word, segment_type, segment_value) "
      + "                                   values (?,?,?,?,?,?,?,?,?,?,?) ";
  
  private static final String insert3Sql = "insert into document_iamge(document_id, image_index, image_index, width, height) "
      + "                                   values (?,?,?,?)";

  private static final String querySql = "select id "
      + "                                   from document "
      + "                                  where hash_code=?";
  
  private static final String select1Sql = "select id, origin_time, origin_name, origin_extension, import_time "
      + "                                   from document "
      + "                                  where hash_code=?";
  
  private static final String select2Sql = "select page_index, segment_id, x0, y0, x1, y1, font_size, word, segment_type, segment_value "
      + "                                   from document_segment "
      + "                                  where document_id=? "
      + "                                  order by x0, y0";
  
  private static final String select3Sql = "select image_index, width, height "
      + "                                   from document_image "
      + "                                  where document_id=? "
      + "                                  order by image_index";


  private IConnectionFactory connFactory;
  
  
  @Reference(name="connFactory")
  void setConnFactory (IConnectionFactory connFactory) {
    this.connFactory = connFactory;
  }
  
  
  void unsetConnFactory (IConnectionFactory connFactory) {
    this.connFactory = null;
  }
  
  

  public void store (Document document) {
    int docId = document.getId();

    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt1 = conn.prepareStatement(insert1Sql);
        IPreparedStatement stmt2 = conn.prepareStatement(insert2Sql);
        IPreparedStatement stmt3 = conn.prepareStatement(insert3Sql))
    {
      stmt1.setInt(docId);
      stmt1.setString(document.getHashCode());
      stmt1.setTimestamp(document.getOriginTime());
      stmt1.setString(document.getOriginName());
      stmt1.setString(document.getOriginExtension());
      stmt1.setTimestamp(document.getImportTime());
      stmt1.executeUpdate();
      
      IDocumentContents docContents = document.getContents();
      
      for (ISegment segment : docContents.getSegments()) {
        stmt2.clearParameters();
        //document_id, page_index, segment_id, x0, y0, x1, y1, font_size, word, segment_type, segment_value) "
        stmt2.setInt(docId);
        stmt2.setInt(segment.getPageIndex());
        stmt2.setInt(segment.getSegmentId());
        stmt2.setFloat(segment.getX0());
        stmt2.setFloat(segment.getY0());
        stmt2.setFloat(segment.getX1());
        stmt2.setFloat(segment.getY1());
        stmt2.setFloat(segment.getFontSize());
        stmt2.setString(segment.getText());
        stmt2.setEnum(segment.getType());
        stmt2.setString(segment.getValue().toString());
        stmt2.executeUpdate();
      }
      
      for (PageImage pageImage : docContents.getPageImages()) {
        stmt3.clearParameters();
        // document_di, image_index, image_index, width, height
        stmt3.setInt(docId);
        stmt3.setInt(pageImage.getImageIndex());
        stmt3.setInt(pageImage.getWidth());
        stmt3.setInt(pageImage.getHeight());
        stmt3.executeUpdate(); 
      }
    }
  }
  
  
  public Document getDeepByHashCode (String hashCode) {
    try (
        IConnection conn = connFactory.getIConnection())
    {
      return getDeepByHashCode (hashCode, conn);
    }
  }
  
  
  private Document getDeepByHashCode (String hashCode, IConnection conn) {
    Document document = getShallowByHashCode(hashCode, conn);
    if (document != null) {
      try (
        IPreparedStatement stmt2 = conn.prepareStatement(select2Sql);
        IPreparedStatement stmt3 = conn.prepareStatement(select3Sql))
      {
        IDocumentContents docContents = new DocumentContents();
        document.setContents(docContents);
        
        // Get all segments of this document
        IResultSet rs2 = stmt2.executeQuery(document.getId());
        while (rs2.next()) {
          int pageIndex = rs2.getInt();
          int segmentId = rs2.getInt();
          float x0 = rs2.getFloat();
          float y0 = rs2.getFloat();
          float x1 = rs2.getFloat();
          float y1 = rs2.getFloat();
          float fontSize = rs2.getFloat();
          String phrase = rs2.getString();
          SegmentType type = rs2.getEnum(SegmentType.class);
          String vx = rs2.getString();
          Object value;
          switch (type) {
          case TEXT :
            value = vx;
            break;
          case DATE :
            value = LocalDate.parse(vx);
            break;
          case CURRENCY :
            value = new Decimal(vx);
            break;
          case COMPANY_NUMBER :
            value = vx;
            break;
          case PERCENT :
            value = new Decimal(vx);
            break;
          default :
            value = vx;
            break;
          }
          Segment segment = new Segment(pageIndex, segmentId, x0, y0, x1, y1, fontSize, phrase, type, value);
          docContents.addSegment(segment);
        }
        rs2.close();

        // Get all page images for this document
        IResultSet rs3 = stmt3.executeQuery(document.getId());
        while (rs3.next()) {
          int imageIndex = rs3.getInt();
          int width = rs3.getInt();
          int height = rs3.getInt();
          PageImage pageImage = new PageImage(imageIndex, width, height);
          docContents.addPageImage(pageImage);
        }
        rs3.close();
      }
    }
    return document;
  }
  

  public Document getShallowByHashCode (String hashCode) {
    try (
        IConnection conn = connFactory.getIConnection()) 
    {
      return getShallowByHashCode(hashCode, conn);
    }
  }
  
  
  private Document getShallowByHashCode (String hashCode, IConnection conn) {
    Document document = null;
    
    try (
        IPreparedStatement stmt = conn.prepareStatement(select1Sql))
    {
      stmt.setString(hashCode);
      IResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int documentId = rs.getInt();
        Timestamp originTime = rs.getTimestamp();
        String originName = rs.getString();
        String originExtension = rs.getString();
        Timestamp importTime = rs.getTimestamp();
        document = new Document(hashCode, originTime, originName, originExtension, importTime, null);
      }
      rs.close();
    }
    return document;
  }

  
  public boolean contains (String hashCode) {
    boolean result;
    
    try (
        IConnection conn = connFactory.getIConnection();
        IPreparedStatement stmt = conn.prepareStatement(querySql))
    {
      stmt.setString(hashCode);
      IResultSet rs = stmt.executeQuery();
      result = rs.next();
      rs.close();
    }
    return result;
  }

}

