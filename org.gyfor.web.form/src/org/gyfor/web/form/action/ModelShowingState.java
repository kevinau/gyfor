package org.gyfor.web.form.action;

import org.gyfor.formref.state.IFormAction;
import org.gyfor.formref.state.IFormState;

public class ModelShowingState implements IFormState {

  private final IFormAction[] actionSet;

  public ModelShowingState(IFormAction... actionSet) {
    this.actionSet = actionSet;
  }
  
  @Override
  public IFormAction[] getActions() {
    return actionSet;
  }

}
