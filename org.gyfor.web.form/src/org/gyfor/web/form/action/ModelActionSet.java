package org.gyfor.web.form.action;

import org.gyfor.formref.state.IFormAction;
import org.gyfor.formref.state.IFormState;

public class ModelActionSet {

  private final IFormAction addAction;
  private final IFormAction clearAction;
  
  private final IFormState showingState;
  private final IFormState addingState;
  private final IFormState clearState;

  public ModelActionSet() {
    
  }
}
