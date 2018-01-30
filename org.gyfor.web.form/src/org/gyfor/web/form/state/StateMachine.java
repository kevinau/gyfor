package org.gyfor.web.form.state;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gyfor.object.model.IEntityModel;

public class StateMachine<S extends Enum<?>, O extends Enum<?>> {

  private S state;
  
  private final TransitionFunction<IEntityModel, S> initialFunction;

  private final List<Transition<S, O, IEntityModel>> transitions = new ArrayList<>();

  private final List<OptionChangeListener> optionChangeListeners = new ArrayList<>();

  private final O[] optionValues;
  
  private final boolean[] availableOptions;
  
  private final boolean[] requiresValidEntry;
  
  
  public StateMachine (Class<S> stateClass, Class<O> optionClass, TransitionFunction<IEntityModel, S> initialFunction) {
    if (!stateClass.isEnum()) {
      throw new IllegalArgumentException("State class is not an Enum");
    }
    if (!optionClass.isEnum()) {
      throw new IllegalArgumentException("Option class is not an Enum");
    }
    optionValues = optionClass.getEnumConstants();
    availableOptions = new boolean[optionValues.length];
    requiresValidEntry = new boolean[optionValues.length];
    
    Field[] optionFields = optionClass.getDeclaredFields();
    for (Field optionField : optionFields) {
      if (optionField.isEnumConstant()) {
        if (optionField.isAnnotationPresent(RequiresValidEntry.class)) {
          O e = valueOf(optionField.getName());
          requiresValidEntry[e.ordinal()] = true;
        }
      }
    }
    
    this.initialFunction = initialFunction;
  }

  
  public void transition(S state, O option, TransitionFunction<IEntityModel, S> function) {
    Transition<S, O, IEntityModel> transition = new Transition<S, O, IEntityModel>(state, option, function);
    transitions.add(transition);
  }
  
  
  public void addOptionChangeListener(OptionChangeListener x) {
    optionChangeListeners.add(x);
  }


  public void removeOptionChangeListener(OptionChangeListener x) {
    optionChangeListeners.remove(x);
  }


  private void fireOptionChanged(O option, boolean available) {
    for (OptionChangeListener x : optionChangeListeners) {
      x.optionChanged(option, available);
    }
  }

  
  private O valueOf(String optionName) {
    for (O option : optionValues) {
      if (option.name().equals(optionName)) {
        return option;
      }
    }
    throw new IllegalArgumentException("Option not known: " + optionName);
  }

  
  public boolean requiresValidEntry(String optionName) {
    O option = valueOf(optionName);
    return requiresValidEntry[option.ordinal()];
  }
  
  
  /**
   * Set the initial state of this FSM.  This method fires option change events for
   * all options that are available.
   */
  public void start(IEntityModel entityModel) {
    state = initialFunction.apply(entityModel);

    // Clear all available options
    Arrays.fill(availableOptions, false);
    
    // Set options that are available for this state
    for (Transition<S, O, ?> t : transitions) {
      if (t.getState() == state) {
        O o = t.getOption();
        availableOptions[o.ordinal()] = true;
      }
    }

    // Fire events for all options that are 'not available'
    for (O o : optionValues) {
      if (!availableOptions[o.ordinal()]) {
        fireOptionChanged(o, false);
      }
    }
    // Then fire event for all options that are 'available'
    for (O o : optionValues) {
      if (availableOptions[o.ordinal()]) {
        fireOptionChanged(o, true);
      }
    }
  }
  
  
  public void setState(S state) {
    this.state = state;
    boolean[] priorOptions = availableOptions.clone();
    
    // Clear all available options
    Arrays.fill(availableOptions, false);
    
    for (Transition<S, O, ?> t : transitions) {
      if (t.getState() == state) {
        O o = t.getOption();
        availableOptions[o.ordinal()] = true;
      }
    }

    // Fire events for all options that have changed to 'not available'
    for (O o : optionValues) {
      if (priorOptions[o.ordinal()] && !availableOptions[o.ordinal()]) {
        fireOptionChanged(o, false);
      }
    }
    // Then fire event for all options that have changed to 'available'
    for (O o : optionValues) {
      if (availableOptions[o.ordinal()] && !priorOptions[o.ordinal()]) {
        fireOptionChanged(o, true);
      }
    }
  }
  
  public void setOption(String optionName, IEntityModel entityModel) {
    for (O option : optionValues) {
      if (option.name().equals(optionName)) {
        // Find transition using the current state and the specified option
        for (Transition<S, O, IEntityModel> t : transitions) {
          if (t.getState() == state && t.getOption() == option) {
            S newState = t.proceed(entityModel);
            setState(newState);
            return;
          }
        }
        throw new IllegalArgumentException("No transition for: state " + state + ", option " + option);
      }
    }
    throw new IllegalArgumentException("Option not known: " + optionName);
  }
  
  
  public S getState() {
    return state;
  }

}
