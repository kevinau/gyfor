package org.gyfor.web.form.state;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gyfor.object.model.IEntityModel;

public class StateMachine2 {

  private Enum<?> state;
  
  private TransitionFunction<IEntityModel, Enum<?>> initialFunction;

  private final List<Transition<Enum<?>, Enum<?>, IEntityModel>> transitions = new ArrayList<>();

  private final List<OptionChangeListener> optionChangeListeners = new ArrayList<>();

  private final Enum<?>[] optionValues;
  
  private final boolean[] availableOptions;
  
  private final boolean[] requiresValidEntry;
  
  
  public StateMachine2 (Class<Enum<?>> stateClass, Class<Enum<?>> optionClass, TransitionFunction<IEntityModel, Enum<?>> initialFunction) {
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
          int i = indexOf(optionField.getName());
          requiresValidEntry[i] = true;
        }
      }
    }
  }

  
  protected void transition(Enum<?> state, Enum<?> option, TransitionFunction<IEntityModel, Enum<?>> function) {
    Transition<Enum<?>, Enum<?>, IEntityModel> transition = new Transition<Enum<?>, Enum<?>, IEntityModel>(state, option, function);
    transitions.add(transition);
  }
  
  
  protected void initialFunction(TransitionFunction<IEntityModel, Enum<?>> initialFunction) {
    this.initialFunction = initialFunction;
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

  
  private int indexOf(String optionName) {
    int i = 0;
    for (Enum<?> o : optionValues) {
      if (o.name().equals(optionName)) {
        return i;
      }
      i++;
    }
    throw new IllegalArgumentException("Option not known: " + optionName);
  }

  
  private int indexOf(Enum<?> option) {
    int i = 0;
    for (Enum<?> o : optionValues) {
      if (o.equals(option)) {
        return i;
      }
      i++;
    }
    throw new IllegalArgumentException("Option not known: " + option);
  }

  
  public boolean requiresValidEntry(String optionName) {
    int i = indexOf(optionName);
    return requiresValidEntry[i];
  }
  
  
  /**
   * Set the initial state of this FSM.  This method fires option change events for
   * all options that are available.
   */
  public void start(IEntityModel entityModel) {
    if (initialFunction == null) {
      throw new IllegalStateException("'initialFunction' not set");
    }
    state = initialFunction.apply(entityModel);

    // Clear all available options
    Arrays.fill(availableOptions, false);
    
    // Set options that are available for this state
    for (Transition<Enum<?>, Enum<?>, ?> t : transitions) {
      if (t.getState() == state) {
        Enum<?> o = t.getOption();
        int i = indexOf(o);
        availableOptions[i] = true;
      }
    }

    // Fire events for all options that are 'not available'
    int i = 0;
    for (Enum<?> o : optionValues) {
      if (!availableOptions[i]) {
        fireOptionChanged(o, false);
      }
      i++;
    }
    // Then fire event for all options that are 'available'
    i = 0;
    for (Enum<?> o : optionValues) {
      if (availableOptions[i]) {
        fireOptionChanged(o, true);
      }
      i++;
    }
  }
  
  
  public void setState(Enum<?> state) {
    this.state = state;
    boolean[] priorOptions = availableOptions.clone();
    
    // Clear all available options
    Arrays.fill(availableOptions, false);
    
    for (Transition<Enum<?>, Enum<?>, ?> t : transitions) {
      if (t.getState() == state) {
        Enum<?> o = t.getOption();
        int i = indexOf(o);
        availableOptions[i] = true;
      }
    }

    // Fire events for all options that have changed to 'not available'
    int i = 0;
    for (Enum<?> o : optionValues) {
      if (priorOptions[i] && !availableOptions[i]) {
        fireOptionChanged(o, false);
      }
      i++;
    }
    // Then fire event for all options that have changed to 'available'
    i = 0;
    for (Enum<?> o : optionValues) {
      if (availableOptions[i] && !priorOptions[i]) {
        fireOptionChanged(o, true);
      }
      i++;
    }
  }
  
  public void setOption(String optionName, IEntityModel entityModel) {
    for (Enum<?> option : optionValues) {
      if (option.name().equals(optionName)) {
        // Find transition using the current state and the specified option
        for (Transition<Enum<?>, Enum<?>, IEntityModel> t : transitions) {
          if (t.getState() == state && t.getOption() == option) {
            Enum<?> newState = t.proceed(entityModel);
            setState(newState);
            return;
          }
        }
        throw new IllegalArgumentException("No transition for: state " + state + ", option " + option);
      }
    }
    throw new IllegalArgumentException("Option not known: " + optionName);
  }
  
  
  public Enum<?> getState() {
    return state;
  }

}
