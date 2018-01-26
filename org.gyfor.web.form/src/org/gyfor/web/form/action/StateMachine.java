package org.gyfor.web.form.action;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

import org.gyfor.object.Entity;
import org.gyfor.object.model.IEntityModel;

public class StateMachine {

  private enum Option {
    FETCH, 
    START_ADD, 
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

  private final List<Transition<State, Option, IEntityModel>> transitions = new ArrayList<>();

  private final List<OptionChangeListener> optionChangeListeners = new ArrayList<>();

  private final EnumSet<Option> availableOptions = EnumSet.noneOf(Option.class);
  
  public StateMachine () {
    transition(State.ADDING, Option.CANCEL, entity -> {
      entity.getClass();
      return State.CLEAR;
    });
    transition(State.ADDING, Option.CONFIRM_ADD, entity -> {
      entity.getClass();
      return State.SHOWING;
    });
    transition(State.CLEAR, Option.FETCH, entity -> {
      entity.getClass();
      return State.SHOWING;
    });
    transition(State.CLEAR, Option.START_ADD, entity -> {
      entity.clear();
      return State.ADDING;
    });
    transition(State.SHOWING, Option.CLEAR, entity -> {
      entity.clear();
      return State.CLEAR;
    });
    transition(State.SHOWING, Option.START_ADD, entity -> {
      entity.clear();
      return State.ADDING;
    });
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
  private void setInitialState(State state) {
    this.state = state;
    availableOptions = EnumSet.noneOf(Option.class);
    
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
  
  
  public State getState() {
    return state;
  }

  public void proceed() {
    this.state.proceed(this);
  }

  private static void sendNotifcation(Entity entity) {
  }

}
