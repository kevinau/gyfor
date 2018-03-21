package org.gyfor.web.form;

import org.plcore.http.ISessionData;

public class DescriptionSessionData implements ISessionData {

  private final String entityClassName;
  
  public DescriptionSessionData(String entityClassName) {
    this.entityClassName = entityClassName;
  }
  
  @Override
  public void startSession() {
    // Send a list of all descriptions
    //stateMachine.start(entityModel);
  }

  @Override
  public void endSession() {
  }
  
  
  public String getEntityClassName() {
    return entityClassName;
  }
  
}
