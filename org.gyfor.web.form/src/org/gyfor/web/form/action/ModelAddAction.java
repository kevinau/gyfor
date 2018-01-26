package org.gyfor.web.form.action;

import org.gyfor.formref.state.IFormAction;
import org.gyfor.formref.state.IFormState;
import org.gyfor.object.model.IEntityModel;

public class ModelAddAction implements IFormAction {

  private final IEntityModel model;
  private final IFormState showingState;
  
  public ModelAddAction(IEntityModel model, IFormState showingState) {
    this.model = model;
    this.showingState = showingState;
  }
  
  @Override
  public String name() {
    return "add";
  }

  @Override
  public void perform(IFormState state) {
    System.out.println("Add instance to the database ....... " + model);
    state.changeState(showingState);
  }

}
