package org.plcore.docstore.search;

import java.time.LocalDate;

public class DocumentReference implements Comparable<DocumentReference> {

  private LocalDate date;
  
  private String documentId;
  
  public DocumentReference(LocalDate date, String documentId) {
    this.date = date;
    this.documentId = documentId;
  }
  
  public LocalDate getDate() {
    return date;
  }
  
  public String getDocumentId() {
    return documentId;
  }

  @Override
  public int compareTo(DocumentReference other) {
    int n = date.compareTo(other.date);
    if (n == 0) {
      return documentId.compareTo(other.documentId);
    } else {
      return n;
    }
  }
  
}
