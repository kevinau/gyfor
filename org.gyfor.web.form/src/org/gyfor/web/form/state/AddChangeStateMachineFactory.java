package org.gyfor.web.form.state;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.IEntityModel;

public class AddChangeStateMachineFactory extends AddOnlyStateMachineFactory {

  protected enum Option {
    FETCH, 
    
    @OptionLabel(label="New", description="Create a new {}")
    START_ADD, 
    
    @OptionLabel(label="Add", description="Save the {}")
    @RequiresValidEntry
    CONFIRM_ADD,
    
    START_EDIT, 
    
    START_CHANGE, 
    
    CONFIRM_CHANGE, 
    
    //START_RETIRE, 
    //CONFIRM_RETIRE, 
    //START_UNRETIRE, 
    //CONFIRM_UNRETIRE, 
    //START_REMOVE, 
    //CONFIRM_REMOVE, 
    CLEAR, 
    
    CANCEL;
  };

  protected enum State {
    CLEAR,
    ADDING,
    SHOWING,
    EDITING,
    CHANGING;
    //RETIRING,
    //UNRETRING,
    //REMOVING;
  };

  
  @SuppressWarnings("unchecked")
  @Override
  public StateMachine<State, Option> getStateMachine() {
    TransitionFunction<IEntityModel, State> startAdding = entityModel -> {
      System.out.println("startAdding ..........");
      entityModel.setEntryMode(EntryMode.ENABLED);
      //Object newInstance = entityModel.newInstance();
      //entityModel.setValue(newInstance);
      return State.ADDING;
    };
    
    TransitionFunction<IEntityModel, State> confirmAdd = entityModel -> {
      System.out.println("confirmAdd ..........");
      entityModel.setEntryMode(EntryMode.DISABLED);
      Object instance = entityModel.getValue();
      System.out.println("       adding " + instance);
      return State.SHOWING;
    };
    
    TransitionFunction<IEntityModel, State> clearForm = entityModel -> {
      System.out.println("clearForm ..........." + entityModel.getName() + " " + entityModel.getNodeId());
      entityModel.setEntryMode(EntryMode.HIDDEN);
      entityModel.setValue(entityModel.newInstance());
      return State.CLEAR;
    };

    StateMachine<State, Option> sm = new StateMachine<State, Option>(State.class, Option.class, clearForm);
    sm.transition(State.CLEAR, Option.START_ADD, startAdding);
    sm.transition(State.ADDING, Option.CANCEL, clearForm);
    sm.transition(State.ADDING, Option.CONFIRM_ADD, confirmAdd);
    sm.transition(State.SHOWING, Option.CLEAR, clearForm);
    sm.transition(State.SHOWING, Option.START_ADD, startAdding);
    return sm;
  }

}
