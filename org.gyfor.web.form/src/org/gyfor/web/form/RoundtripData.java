package org.gyfor.web.form;

import org.gyfor.http.ISessionData;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.web.form.action.StateMachine;

public class RoundtripData implements ISessionData {

  private final IEntityModel entityModel;
  
  private final StateMachine stateMachine;

  public RoundtripData(IEntityModel entityModel, StateMachine stateMachine) {
    this.entityModel = entityModel;
    this.stateMachine = stateMachine;
  }
  
  public IEntityModel entityModel() {
    return entityModel;
  }
  
  public StateMachine stateMachine() {
    return stateMachine;
  }

  @Override
  public void startSession() {
    stateMachine.start(entityModel);
  }

  @Override
  public void endSession() {
  }
  
}
