package org.gyfor.web.form.action;

import org.gyfor.formref.state.IFormAction;

public class ModelEditReadyState {

  private final IFormAction cancelAction;
  
  @Override
  public IFormAction[] getActions() {
    return new IFormAction[] {
        cancelAction,
    };
  }
  

}
