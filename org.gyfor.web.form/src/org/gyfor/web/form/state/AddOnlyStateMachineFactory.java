package org.gyfor.web.form.state;

import java.util.Map;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.IEntityModel;
import org.gyfor.object.model.IModelFactory;
import org.osgi.service.component.annotations.Reference;


public class AddOnlyStateMachineFactory implements IStateMachineFactory {

  @Reference 
  private IModelFactory modelFactory;
  
  
  private enum Action {
    @ActionLabel(label="New", description="Create a new {}")
    START_ADD, 
    
    @ActionLabel(label="Add", description="Save the {}")
    @RequiresValidEntry
    CONFIRM_ADD,
    
    CLEAR, 
    
    CANCEL;
  };


  private enum State {
    CLEAR,
    ADDING,
    SHOWING;
  };



  @Override
  public StateMachine<State, Action> getStateMachine(Map<String, Object> props) {
    String entityClassName = (String)props.get("entity");
    if (entityClassName == null) {
      throw new IllegalArgumentException("No 'entity' property");
    }
    IEntityModel entityModel;
    try {
      entityModel = modelFactory.buildEntityModel(entityClassName);
    } catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException(ex);
    }
    
    TransitionFunction<State> startAdding = () -> {
      entityModel.setEntryMode(EntryMode.ENABLED);
      //Object newInstance = entityModel.newInstance();
      //entityModel.setValue(newInstance);
      return State.ADDING;
    };
    
    TransitionFunction<State> confirmAdd = () -> {
      entityModel.setEntryMode(EntryMode.DISABLED);
      Object instance = entityModel.getValue();
      System.out.println("       adding " + instance);
      return State.SHOWING;
    };
    
    TransitionFunction<State> clearForm = () -> {
      entityModel.setEntryMode(EntryMode.HIDDEN);
      entityModel.setValue(entityModel.newInstance());
      return State.CLEAR;
    };

    StateMachine<State, Action> sm = new StateMachine<State, Action>(State.class, Action.class, startAdding);
    sm.addTransition(State.CLEAR, Action.START_ADD, startAdding);
    sm.addTransition(State.ADDING, Action.CANCEL, clearForm);
    sm.addTransition(State.ADDING, Action.CONFIRM_ADD, confirmAdd);
    sm.addTransition(State.SHOWING, Action.CLEAR, clearForm);
    sm.addTransition(State.SHOWING, Action.START_ADD, startAdding);
    return sm;
  }

}
