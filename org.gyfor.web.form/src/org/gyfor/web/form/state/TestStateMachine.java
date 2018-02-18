package org.gyfor.web.form.state;


enum Action {
  @ActionLabel(label="New", description="Create a new {}")
  START_ADD, 
  
  @ActionLabel(label="Add", description="Save the {}")
  @RequiresValidEntry
  CONFIRM_ADD,
  
  CANCEL;
};

enum State {
  ADDING;
};


public class TestStateMachine extends StateMachine<State, Action> {

  public TestStateMachine(Class<State> stateClass, Class<Action> actionClass,
      TransitionFunction<State> initialFunction) {
    super(stateClass, actionClass, initialFunction);
    // TODO Auto-generated constructor stub
  }

}
