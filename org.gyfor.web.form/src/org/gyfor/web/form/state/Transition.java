package org.gyfor.web.form.state;

public class Transition<S extends Enum<?>, A extends Enum<?>> {
  
  private final S state;
  private final A action;
  private final TransitionFunction<S> function;
  
  public Transition(S state, A action, TransitionFunction<S> function) {
    this.state = state;
    this.action = action;
    this.function = function;
  }

  public S getState() {
    return state;
  }
  
  public A getAction() {
    return action;
  }
  
  
  public S proceed() {
    return function.apply();
  }
  
}
