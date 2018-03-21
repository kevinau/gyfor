package org.plcore.docstore.search;

import java.util.List;


public interface ISearchEngine {

  public List<DocumentReference> searchIndex(String queryString);

}
