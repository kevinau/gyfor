package org.gyfor.web.form.state;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gyfor.object.model.IEntityModel;

public class StateMachine<S extends Enum<S>, A extends Enum<A>> {

  private S state;
  
  private final TransitionFunction<S> initialFunction;

  private final List<Transition<S, A>> transitions = new ArrayList<>();

  private final List<ActionChangeListener> actionChangeListeners = new ArrayList<>();

  private final A[] actionValues;
  
  private final boolean[] availableActions;
  
  private final boolean[] requiresValidEntry;
  
  
  public StateMachine (Class<S> stateClass, Class<A> actionClass, TransitionFunction<S> initialFunction) {
    if (!stateClass.isEnum()) {
      throw new IllegalArgumentException("State class is not an Enum");
    }
    if (!actionClass.isEnum()) {
      throw new IllegalArgumentException("Action class is not an Enum");
    }
    actionValues = actionClass.getEnumConstants();
    availableActions = new boolean[actionValues.length];
    requiresValidEntry = new boolean[actionValues.length];
    
    Field[] actionFields = actionClass.getDeclaredFields();
    for (Field actionField : actionFields) {
      if (actionField.isEnumConstant()) {
        if (actionField.isAnnotationPresent(RequiresValidEntry.class)) {
          A e = valueOf(actionField.getName());
          requiresValidEntry[e.ordinal()] = true;
        }
      }
    }
    
    this.initialFunction = initialFunction;
  }

  
  public void addTransition(S state, A action, TransitionFunction<S> function) {
    Transition<S, A> transition = new Transition<S, A>(state, action, function);
    transitions.add(transition);
  }
  
  
  public void addActionChangeListener(ActionChangeListener x) {
    actionChangeListeners.add(x);
  }


  public void removeActionChangeListener(ActionChangeListener x) {
    actionChangeListeners.remove(x);
  }


  private void fireActionChanged(A action, boolean available) {
    for (ActionChangeListener x : actionChangeListeners) {
      x.actionChanged(action, available);
    }
  }

  
  public A valueOf(String actionName) {
    for (A action : actionValues) {
      if (action.name().equals(actionName)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Action not known: " + actionName);
  }

  
  public boolean requiresValidEntry(String actionName) {
    A action = valueOf(actionName);
    return requiresValidEntry[action.ordinal()];
  }
  
  
  /**
   * Set the initial state of this FSM.  This method fires action change events for
   * all actions that are available.
   */
  public void start(Object... args) {
    state = initialFunction.apply();

    // Clear all available actions
    Arrays.fill(availableActions, false);
    
    // Set actions that are available for this state
    for (Transition<S, A> t : transitions) {
      if (t.getState() == state) {
        A o = t.getAction();
        availableActions[o.ordinal()] = true;
      }
    }

    // Fire events for all actions that are 'not available'
    for (A o : actionValues) {
      if (!availableActions[o.ordinal()]) {
        fireActionChanged(o, false);
      }
    }
    // Then fire event for all actions that are 'available'
    for (A o : actionValues) {
      if (availableActions[o.ordinal()]) {
        fireActionChanged(o, true);
      }
    }
  }
  
  
  public void transition (S onState, TransitionFunction<S> function) {
    if (state == onState) {
      S newState = function.apply();
      setState(newState);
    }
  }
  
  
  public void setState(S state) {
    this.state = state;
    boolean[] priorActions = availableActions.clone();
    
    // Clear all available actions
    Arrays.fill(availableActions, false);
    
    for (Transition<S, A> t : transitions) {
      if (t.getState() == state) {
        A o = t.getAction();
        availableActions[o.ordinal()] = true;
      }
    }

    // Fire events for all actions that have changed to 'not available'
    for (A o : actionValues) {
      if (priorActions[o.ordinal()] && !availableActions[o.ordinal()]) {
        fireActionChanged(o, false);
      }
    }
    // Then fire event for all actions that have changed to 'available'
    for (A o : actionValues) {
      if (availableActions[o.ordinal()] && !priorActions[o.ordinal()]) {
        fireActionChanged(o, true);
      }
    }
  }
  
  public void setAction(String actionName, IEntityModel entityModel) {
    for (A action : actionValues) {
      if (action.name().equals(actionName)) {
        // Find transition using the current state and the specified action
        for (Transition<S, A> t : transitions) {
          if (t.getState() == state && t.getAction() == action) {
            S newState = t.proceed();
            setState(newState);
            return;
          }
        }
        throw new IllegalArgumentException("No transition for: state " + state + ", action " + action);
      }
    }
    throw new IllegalArgumentException("Action not known: " + actionName);
  }
  
  
  public S getState() {
    return state;
  }

}
