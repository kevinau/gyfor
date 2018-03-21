package org.plcore.userio.model.state;

public class Transition<S extends Enum<?>, O extends Enum<?>, A> {
  
  private final S state;
  private final O option;
  private final TransitionFunction<A, S> function;
  
  public Transition(S state, O option, TransitionFunction<A, S> function) {
    this.state = state;
    this.option = option;
    this.function = function;
  }

  public S getState() {
    return state;
  }
  
  public O getOption() {
    return option;
  }
  
  
  public S proceed(A arg) {
    return function.apply(arg);
  }
  
}
