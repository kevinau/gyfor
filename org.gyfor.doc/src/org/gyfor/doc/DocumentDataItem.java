package org.gyfor.doc;


public class DocumentDataItem {

  /**
   * Path into the document type object
   */
  private String path;
  
  /**
   * Id that identifies the segment, and hence value, that will be assigned
   * to the document type object.  This will be -1 if the value is entered.
   */
  private int segmentId;
  
  /**
   * An entered or segment value.
   */
  private Object value;
  
}
