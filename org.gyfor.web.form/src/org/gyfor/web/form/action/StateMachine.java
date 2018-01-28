package org.gyfor.web.form.action;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

import org.gyfor.object.EntryMode;
import org.gyfor.object.model.IEntityModel;

public class StateMachine {

  private enum Option {
    //FETCH, 
    @OptionLabel(label="New", description="Create a new {}")
    START_ADD, 
    
    @OptionLabel(label="Add", description="Save the {}")
    CONFIRM_ADD,
    
    //START_EDIT, 
    //START_CHANGE, 
    //CONFIRM_CHANGE, 
    //START_RETIRE, 
    //CONFIRM_RETIRE, 
    //START_UNRETIRE, 
    //CONFIRM_UNRETIRE, 
    //START_REMOVE, 
    //CONFIRM_REMOVE, 
    CLEAR, 
    
    CANCEL;
  };

  private enum State {
    CLEAR,
    ADDING,
    SHOWING;
    //EDITING,
    //CHANGING,
    //RETIRING,
    //UNRETRING,
    //REMOVING;
  
  };

  private State state;
  
  private final Function<IEntityModel, State> initialFunction;

  private final List<Transition<State, Option, IEntityModel>> transitions = new ArrayList<>();

  private final List<OptionChangeListener> optionChangeListeners = new ArrayList<>();

  private final EnumSet<Option> availableOptions = EnumSet.noneOf(Option.class);
  
  public StateMachine () {
    Function<IEntityModel, State> startAdding = entityModel -> {
      System.out.println("startAdding ..........");
      entityModel.setEntryMode(EntryMode.ENABLED);
      //Object newInstance = entityModel.newInstance();
      //entityModel.setValue(newInstance);
      return State.ADDING;
    };
    
    Function<IEntityModel, State> confirmAdd = entityModel -> {
      System.out.println("confirmAdd ..........");
      entityModel.setEntryMode(EntryMode.DISABLED);
      Object instance = entityModel.getValue();
      System.out.println("       adding " + instance);
      return State.SHOWING;
    };
    
    Function<IEntityModel, State> clearForm = entityModel -> {
      System.out.println("clearForm ..........." + entityModel.getName() + " " + entityModel.getNodeId());
      entityModel.setEntryMode(EntryMode.HIDDEN);
      entityModel.setValue(entityModel.newInstance());
      return State.CLEAR;
    };
    
    initialFunction = clearForm;
    
    transition(State.CLEAR, Option.START_ADD, startAdding);
    transition(State.ADDING, Option.CANCEL, clearForm);
    transition(State.ADDING, Option.CONFIRM_ADD, confirmAdd);
    transition(State.SHOWING, Option.CLEAR, clearForm);
    transition(State.SHOWING, Option.START_ADD, startAdding);
  }

  
  private void transition(State state, Option option, Function<IEntityModel, State> function) {
    Transition<State, Option, IEntityModel> transition = new Transition<>(state, option, function);
    transitions.add(transition);
  }
  
  
  public void addOptionChangeListener(OptionChangeListener x) {
    optionChangeListeners.add(x);
  }


  public void removeOptionChangeListener(OptionChangeListener x) {
    optionChangeListeners.remove(x);
  }


  private void fireOptionChanged(Enum<?> option, boolean available) {
    for (OptionChangeListener x : optionChangeListeners) {
      x.optionChanged(option, available);
    }
  }

  
  /**
   * Set the initial state of this FSM.  This method fires option change events for
   * all options that are available.
   */
  public void start(IEntityModel entityModel) {
    state = initialFunction.apply(entityModel);
  
    availableOptions.clear();
    for (Transition<State, Option, ?> t : transitions) {
      if (t.getState() == state) {
        Option o = t.getOption();
        availableOptions.add(o);
      }
    }

    // Fire events for all options that are 'not available'
    for (Option o : Option.values()) {
      if (!availableOptions.contains(o)) {
        fireOptionChanged(o, false);
      }
    }
    // Then fire event for all options that are 'available'
    for (Option o : availableOptions) {
      fireOptionChanged(o, true);
    }
  }
  
  
  public void setState(State state) {
    this.state = state;
    EnumSet<Option> priorOptions = availableOptions.clone();
    
    availableOptions.clear();
    for (Transition<State, Option, ?> t : transitions) {
      if (t.getState() == state) {
        Option o = t.getOption();
        availableOptions.add(o);
      }
    }

    // Fire events for all options that have changed to 'not available'
    for (Option o : Option.values()) {
      if (priorOptions.contains(o) && !availableOptions.contains(o)) {
        fireOptionChanged(o, false);
      }
    }
    // Then fire event for all options that have changed to 'available'
    for (Option o : Option.values()) {
      if (availableOptions.contains(o) && !priorOptions.contains(o)) {
        fireOptionChanged(o, true);
      }
    }
  }
  
  public void setOption(String optionName, IEntityModel entityModel) {
    Option option = Option.valueOf(optionName);
    // Find transition using the current state and the specified option
    for (Transition<State, Option, IEntityModel> t : transitions) {
      if (t.getState() == state && t.getOption() == option) {
        State newState = t.proceed(entityModel);
        setState(newState);
        return;
      }
    }
    throw new IllegalArgumentException("Option not known: " + optionName);
  }
  
  
  public State getState() {
    return state;
  }

}
