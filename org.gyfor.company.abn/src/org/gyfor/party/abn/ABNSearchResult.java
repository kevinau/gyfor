package org.gyfor.party.abn;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Node;


public class ABNSearchResult {

  private Node responseNode;
  

  public ABNSearchResult(Node searchResultsPayload) {
    this.responseNode = XMLUtils.getNode(searchResultsPayload, "/ABRPayloadSearchResults/response");
  }


  public boolean isException() {
    return XMLUtils.getNode(responseNode, "exception") != null;
  }


  public String getOrganisationName() throws SecurityException, IllegalArgumentException, ClassNotFoundException,
      NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return XMLUtils.getNodeText(responseNode, "businessEntity/mainName/organisationName");
  }


  public String getExceptionDescription() throws SecurityException, IllegalArgumentException, ClassNotFoundException,
      NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    return XMLUtils.getNodeText(responseNode, "exception/exceptionDescription");
  }

}
