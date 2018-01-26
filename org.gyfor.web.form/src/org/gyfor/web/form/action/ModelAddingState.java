package org.gyfor.web.form.action;

import org.gyfor.formref.state.IFormAction;
import org.gyfor.formref.state.IFormState;

public class ModelAddingState implements IFormState {

  private final IFormAction addAction;
  private final IFormAction cancelAction;
  
  @Override
  public IFormAction[] getActions() {
    return new IFormAction[] {
        addAction,
        cancelAction,
    };
  }
  
}
