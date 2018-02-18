package org.gyfor.web.form.state;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gyfor.object.model.IEntityModel;

public class StateMachine2 {

  private Enum<?> state;
  
  private TransitionFunction<Enum<?>> initialFunction;

  private final List<Transition<Enum<?>, Enum<?>>> transitions = new ArrayList<>();

  private final List<ActionChangeListener> actionChangeListeners = new ArrayList<>();

  private final Enum<?>[] actionValues;
  
  private final boolean[] availableActions;
  
  private final boolean[] requiresValidEntry;
  
  
  public StateMachine2 (Class<Enum<?>> stateClass, Class<Enum<?>> actionClass, TransitionFunction<Enum<?>> initialFunction) {
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
          int i = indexOf(actionField.getName());
          requiresValidEntry[i] = true;
        }
      }
    }
  }

  
  protected void transition(Enum<?> state, Enum<?> action, TransitionFunction<Enum<?>> function) {
    Transition<Enum<?>, Enum<?>> transition = new Transition<Enum<?>, Enum<?>>(state, action, function);
    transitions.add(transition);
  }
  
  
  protected void initialFunction(TransitionFunction<Enum<?>> initialFunction) {
    this.initialFunction = initialFunction;
  }
  
  
  public void addActionChangeListener(ActionChangeListener x) {
    actionChangeListeners.add(x);
  }


  public void removeActionChangeListener(ActionChangeListener x) {
    actionChangeListeners.remove(x);
  }


  private void fireActionChanged(Enum<?> action, boolean available) {
    for (ActionChangeListener x : actionChangeListeners) {
      x.actionChanged(action, available);
    }
  }

  
  private int indexOf(String actionName) {
    int i = 0;
    for (Enum<?> a : actionValues) {
      if (a.name().equals(actionName)) {
        return i;
      }
      i++;
    }
    throw new IllegalArgumentException("Action not known: " + actionName);
  }

  
  private int indexOf(Enum<?> action) {
    int i = 0;
    for (Enum<?> a : actionValues) {
      if (a.equals(action)) {
        return i;
      }
      i++;
    }
    throw new IllegalArgumentException("Action not known: " + action);
  }

  
  public boolean requiresValidEntry(String actionName) {
    int i = indexOf(actionName);
    return requiresValidEntry[i];
  }
  
  
  /**
   * Set the initial state of this FSM.  This method fires action change events for
   * all actions that are available.
   */
  public void start(IEntityModel entityModel) {
    if (initialFunction == null) {
      throw new IllegalStateException("'initialFunction' not set");
    }
    state = initialFunction.apply();

    // Clear all available actions
    Arrays.fill(availableActions, false);
    
    // Set actions that are available for this state
    for (Transition<Enum<?>, Enum<?>> t : transitions) {
      if (t.getState() == state) {
        Enum<?> a = t.getAction();
        int i = indexOf(a);
        availableActions[i] = true;
      }
    }

    // Fire events for all actions that are 'not available'
    int i = 0;
    for (Enum<?> a : actionValues) {
      if (!availableActions[i]) {
        fireActionChanged(a, false);
      }
      i++;
    }
    // Then fire event for all actions that are 'available'
    i = 0;
    for (Enum<?> a : actionValues) {
      if (availableActions[i]) {
        fireActionChanged(a, true);
      }
      i++;
    }
  }
  
  
  public void setState(Enum<?> state) {
    this.state = state;
    boolean[] priorActions = availableActions.clone();
    
    // Clear all available actions
    Arrays.fill(availableActions, false);
    
    for (Transition<Enum<?>, Enum<?>> t : transitions) {
      if (t.getState() == state) {
        Enum<?> a = t.getAction();
        int i = indexOf(a);
        availableActions[i] = true;
      }
    }

    // Fire events for all actions that have changed to 'not available'
    int i = 0;
    for (Enum<?> a : actionValues) {
      if (priorActions[i] && !availableActions[i]) {
        fireActionChanged(a, false);
      }
      i++;
    }
    // Then fire event for all actions that have changed to 'available'
    i = 0;
    for (Enum<?> a : actionValues) {
      if (availableActions[i] && !priorActions[i]) {
        fireActionChanged(a, true);
      }
      i++;
    }
  }
  
  public void setAction(String actionName, IEntityModel entityModel) {
    for (Enum<?> action : actionValues) {
      if (action.name().equals(actionName)) {
        // Find transition using the current state and the specified action
        for (Transition<Enum<?>, Enum<?>> t : transitions) {
          if (t.getState() == state && t.getAction() == action) {
            Enum<?> newState = t.proceed();
            setState(newState);
            return;
          }
        }
        throw new IllegalArgumentException("No transition for: state " + state + ", action " + action);
      }
    }
    throw new IllegalArgumentException("Action not known: " + actionName);
  }
  
  
  public Enum<?> getState() {
    return state;
  }

}
