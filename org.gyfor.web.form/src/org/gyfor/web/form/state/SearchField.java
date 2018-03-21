package org.gyfor.web.form.state;

import java.util.List;

import org.gyfor.dao.EntityData;
import org.gyfor.dao.IDataAccessObject;
import org.plcore.userio.Optional;
import org.plcore.userio.UserEntryException;

public class SearchField {

  private final IDataAccessObject dao;
  private final String objectName;
  
  private String searchText = "";
  
  public SearchField(IDataAccessObject dao, String objectName) {
    this.dao = dao;
    this.objectName = objectName;
  }
  
  public String getSearchText() {
    return searchText;
  }
  
  @Optional(false)
  public void setSearchText(String searchText) {
    this.searchText = searchText;
  }
  
  public void checkSearchText() throws UserEntryException {
    List<EntityData> found = dao.search(searchText);      
    switch (found.size()) {
    case 0 :
      if (searchText.length() == 0) {
        throw new UserEntryException("No " + objectName, UserEntryException.Type.ERROR);
      } else {
        throw new UserEntryException("No " + objectName + " found", UserEntryException.Type.ERROR);
      }
    case 1 :
      return;
    default :
      if (searchText.length() == 0) {
        throw new UserEntryException("Required", UserEntryException.Type.REQUIRED);
      } else {
        throw new UserEntryException("Multiple " + objectName + " found", UserEntryException.Type.INCOMPLETE);
      }
    }
  }
  
}
