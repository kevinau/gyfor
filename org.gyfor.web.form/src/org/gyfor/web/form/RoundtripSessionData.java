package org.gyfor.web.form;

import org.gyfor.web.form.state.StateMachine;
import org.plcore.http.ISessionData;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ItemEventAdapter;

public class RoundtripSessionData implements ISessionData {

  private final IEntityModel entityModel;
  
  private final StateMachine<?,?> stateMachine;

  public RoundtripSessionData(IEntityModel entityModel, StateMachine<?,?> stateMachine) {
    this.entityModel = entityModel;
    this.stateMachine = stateMachine;
  }
  
  public IEntityModel entityModel() {
    return entityModel;
  }
  
  public StateMachine<?,?> stateMachine() {
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
